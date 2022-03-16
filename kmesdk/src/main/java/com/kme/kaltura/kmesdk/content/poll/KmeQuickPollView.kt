package com.kme.kaltura.kmesdk.content.poll

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.content.poll.type.KmeQuickPollTypeView
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.util.messages.buildSendQuickPollAnswerMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollAudienceType
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType
import kotlinx.coroutines.*
import org.koin.core.inject
import java.util.*

class KmeQuickPollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    KmeKoinComponent,
    IKmeQuickPollView,
    KmeQuickPollTypeView.OnAnswerListener,
    KmeQuickPollResultsView.OnCloseResultsListener {

    private val RESULTS_HIDE_TIMEOUT = 20_000L

    var state: State = State.GONE
        private set

    private lateinit var config: Config
    private lateinit var lifecycleOwner: LifecycleOwner

    private val defaultEventHandler: KmeDefaultPollEventHandler by inject()
    private val userController: IKmeUserController by inject()
    private val roomController: IKmeRoomController by scopedInject()

    private var hideResultsViewJob: Job? = null

    private var pollView: KmeQuickPollTypeView<out ViewBinding>? = null
    private var pollResultsView: KmeQuickPollResultsView? = null
    private var startPollPayload: QuickPollStartedPayload? = null
    private var endPollPayload: QuickPollEndedPayload? = null
    private var prevAnswerType: Int? = null

    private val uiScope = CoroutineScope(Dispatchers.Main)

    init {
        visibility = GONE
        setBackgroundColor(ContextCompat.getColor(context, R.color.transparentColor1))
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            this.visibility = state.getInt(SAVE_VISIBILITY_KEY)
            this.startPollPayload =
                state.getSerializable(SAVE_START_PAYLOAD_KEY) as QuickPollStartedPayload?
            this.endPollPayload =
                state.getSerializable(SAVE_END_PAYLOAD_KEY) as QuickPollEndedPayload?
            this.state = state.getSerializable(SAVE_STATE_KEY) as State
            this.prevAnswerType = state.getInt(SAVE_PREV_ANSWER_TYPE_KEY, -1)

            restoreState()

            super.onRestoreInstanceState(state.getParcelable(SAVE_SUPER_STATE_KEY))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun init(lifecycleOwner: LifecycleOwner, config: Config) {
        this.config = config
        this.lifecycleOwner = lifecycleOwner

        if (config.useDefaultHandler) {
            setupDefaultEventHandler()
        }
    }

    private fun setupDefaultEventHandler() {
        defaultEventHandler.pollStartedLiveData.observe(lifecycleOwner) { it?.let { startPoll(it) } }
        defaultEventHandler.pollEndedLiveData.observe(lifecycleOwner) { it?.let { endPoll(it) } }
        defaultEventHandler.userAnsweredPollLiveData.observe(lifecycleOwner) {
            it?.let { onUserAnsweredPoll(it) }
        }
        defaultEventHandler.subscribe()
    }

    private fun restoreState() {
        if (visibility != GONE) {
            endPollPayload?.let {
                endPoll(it)
            } ?: startPollPayload?.let {
                startPoll(it)
            }
        } else {
            removeAllViews()
            state = State.GONE
            visibility = GONE
        }
    }

    override fun sendAnswer(answer: QuickPollPayload.Answer) {
        roomController.send(
            buildSendQuickPollAnswerMessage(
                answer,
                roomController.roomSettings?.roomInfo?.id ?: 0L,
                roomController.roomSettings?.roomInfo?.companyId ?: 0L,
            )
        )
        prevAnswerType = null
        visibility = GONE
        removeAllViews()
        state = State.WAITING_FOR_RESULT
    }

    override fun startPoll(payload: QuickPollStartedPayload) {
        hideResultsViewJob?.cancel()

        visibility = VISIBLE
        removeAllViews()
        startPollPayload = payload
        endPollPayload = null

        val isModerator = userController.isModerator()
        if (payload.targetAudience == KmeQuickPollAudienceType.NON_MODERATORS && isModerator) {
            state = State.MODERATOR_RESULT_VIEW
            showResultsView()
        } else {
            state = State.WAITING_FOR_ANSWER_VIEW
            pollView = KmeQuickPollTypeView.getView(context, payload.type)?.apply {
                isAnonymousPoll = payload.isAnonymous == true
                listener = this@KmeQuickPollView
                savedAnswerType = this@KmeQuickPollView.prevAnswerType
            }

            pollView?.let { addView(it) }
        }
    }

    override fun endPoll(payload: QuickPollEndedPayload) {
        endPollPayload = payload
        if (payload.shouldPresent == true) {
            state = State.RESULT_VIEW
            showResultsView(payload)
        } else {
            visibility = GONE
            state = State.GONE
            removeAllViews()
            pollView = null
            startPollPayload = null
            endPollPayload = null
        }
    }

    private fun onUserAnsweredPoll(payload: QuickPollUserAnsweredPayload) {
        applyResult(
            QuickPollPayload.Answer(
                payload.answer,
                payload.pollId,
                payload.userId
            )
        )
    }

    override fun applyResult(answer: QuickPollPayload.Answer) {
        pollResultsView?.apply {
            applyAnswer(answer)
        } ?: run {
            showResultsView(null, answer)
        }
    }

    override fun applyResults(answers: List<QuickPollPayload.Answer>?) {
        pollResultsView?.apply {
            applyAnswers(answers)
        } ?: run {
            showResultsView(null, null, answers)
        }
    }

    override fun onAnswered(
        type: KmeQuickPollType,
        answer: Int
    ) {
        sendAnswer(QuickPollPayload.Answer(answer, startPollPayload?.pollId))
    }

    override fun showResultsView(
        payload: QuickPollEndedPayload?,
        answer: QuickPollPayload.Answer?,
        answers: List<QuickPollPayload.Answer>?
    ) {
        removeAllViews()
        startPollPayload?.let {
            visibility = VISIBLE
            pollView = null
            pollResultsView = KmeQuickPollResultsView(context).also { resultsView ->
                resultsView.closeListener = this
                resultsView.init(it, payload)
                addView(resultsView)

                if (answer != null) {
                    resultsView.post { resultsView.applyAnswer(answer) }
                } else if (answers != null) {
                    resultsView.post { resultsView.applyAnswers(answers) }
                }

                if (state == State.RESULT_VIEW) {
                    hideResultsViewJob?.cancel()
                    hideResultsViewJob = uiScope.launch {
                        delay(RESULTS_HIDE_TIMEOUT)
                        onCloseResultsView()
                    }
                }
            }
        }
    }

    override fun onCloseResultsView() {
        removeAllViews()
        state = State.GONE
        visibility = GONE
        startPollPayload = null
        endPollPayload = null
        pollResultsView = null
        hideResultsViewJob?.cancel()
        hideResultsViewJob = null
    }

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putInt(SAVE_VISIBILITY_KEY, visibility)
            putSerializable(SAVE_STATE_KEY, state)
            putSerializable(SAVE_START_PAYLOAD_KEY, startPollPayload)
            putSerializable(SAVE_END_PAYLOAD_KEY, endPollPayload)
            putInt(SAVE_PREV_ANSWER_TYPE_KEY, pollView?.prevAnswerType ?: -1)
            putParcelable(SAVE_SUPER_STATE_KEY, super.onSaveInstanceState())
        }
    }

    override fun onDetachedFromWindow() {
        removeAllViews()
        visibility = GONE

        if (::config.isInitialized && config.useDefaultHandler) {
            defaultEventHandler.release()
        }

        startPollPayload = null
        pollView = null
        pollResultsView = null

        hideResultsViewJob?.cancel()
        hideResultsViewJob = null

        super.onDetachedFromWindow()
    }

    enum class State {
        GONE, RESULT_VIEW, MODERATOR_RESULT_VIEW, WAITING_FOR_ANSWER_VIEW, WAITING_FOR_RESULT
    }

    class Config {
        var useDefaultHandler = true
    }

    companion object {
        private const val SAVE_VISIBILITY_KEY = "SAVE_VISIBILITY_KEY"
        private const val SAVE_START_PAYLOAD_KEY = "SAVE_START_PAYLOAD_KEY"
        private const val SAVE_END_PAYLOAD_KEY = "SAVE_END_PAYLOAD_KEY"
        private const val SAVE_STATE_KEY = "SAVE_STATE_KEY"
        private const val SAVE_PREV_ANSWER_TYPE_KEY = "SAVE_PREV_ANSWER_TYPE_KEY"
        private const val SAVE_SUPER_STATE_KEY = "SAVE_SUPER_STATE_KEY"
    }

}