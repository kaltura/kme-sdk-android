package com.kme.kaltura.kmesdk.content.poll

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.content.poll.type.KmeQuickPollTypeView
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.util.messages.buildSendQuickPollAnswerMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollAudienceType
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType
import kotlinx.coroutines.*
import org.koin.core.inject
import androidx.lifecycle.Observer
import java.util.*

class KmeQuickPollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    KmeKoinComponent, IKmeQuickPollView,
    KmeQuickPollTypeView.OnAnswerListener, KmeQuickPollResultsView.OnCloseResultsListener {

    private val RESULTS_HIDE_TIMEOUT = 20_000L

    var state: State = State.GONE
        private set

    private lateinit var config: Config

    private val defaultEventHandler: KmeDefaultPollEventHandler by inject()
    private val roomController: IKmeRoomController by inject()
    private val userController: IKmeUserController by inject()

    private var hideResultsViewJob: Job? = null

    private var pollView: KmeQuickPollTypeView<out ViewBinding>? = null
    private var pollResultsView: KmeQuickPollResultsView? = null
    private var currentPollPayload: QuickPollStartedPayload? = null

    private val uiScope = CoroutineScope(Dispatchers.Main)

    init {
        visibility = GONE
        setBackgroundColor(ContextCompat.getColor(context, R.color.transparentColor1))
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            visibility = state.getInt(SAVE_VISIBILITY_KEY)
            super.onRestoreInstanceState(state.getParcelable(SAVE_SUPER_STATE_KEY))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun init(config: Config) {
        this.config = config

        if (config.useDefaultHandler) {
            setupDefaultEventHandler()
        }
    }

    private fun setupDefaultEventHandler() {
        defaultEventHandler.pollStartedLiveData.observeForever { it?.let { startPoll(it) } }
        defaultEventHandler.pollEndedLiveData.observeForever { it?.let { endPoll(it) } }
        defaultEventHandler.userAnsweredPollLiveData.observeForever {
            it?.let { onUserAnsweredPoll(it) }
        }
        defaultEventHandler.subscribe()
    }

    override fun sendAnswer(answer: QuickPollPayload.Answer) {
        roomController.send(
            buildSendQuickPollAnswerMessage(
                answer,
                roomController.roomSettings?.roomInfo?.id ?: 0L,
                roomController.roomSettings?.roomInfo?.companyId ?: 0L,
            )
        )

        visibility = GONE
        removeAllViews()
        state = State.WAITING_FOR_RESULT
    }

    override fun startPoll(payload: QuickPollStartedPayload) {
        hideResultsViewJob?.cancel()

        visibility = VISIBLE
        removeAllViews()
        currentPollPayload = payload

        val isModerator = userController.isModerator()
        if (payload.targetAudience == KmeQuickPollAudienceType.NON_MODERATORS && isModerator) {
            state = State.MODERATOR_RESULT_VIEW
            showResultsView()
        } else {
            state = State.WAITING_FOR_ANSWER_VIEW
            pollView = KmeQuickPollTypeView.getView(context, payload.type)?.apply {
                isAnonymousPoll = payload.isAnonymous == true
                listener = this@KmeQuickPollView
            }

            pollView?.let { addView(it) }
        }
    }

    override fun endPoll(payload: QuickPollEndedPayload) {
        if (payload.shouldPresent == true) {
            state = State.RESULT_VIEW
            showResultsView(payload)
        } else {
            visibility = GONE
            state = State.GONE
            removeAllViews()
            pollView = null
            currentPollPayload = null
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
        sendAnswer(QuickPollPayload.Answer(answer, currentPollPayload?.pollId))
    }

    override fun showResultsView(
        payload: QuickPollEndedPayload?,
        answer: QuickPollPayload.Answer?,
        answers: List<QuickPollPayload.Answer>?
    ) {
        removeAllViews()
        currentPollPayload?.let {
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
        pollResultsView = null
        hideResultsViewJob?.cancel()
        hideResultsViewJob = null
    }

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putInt(SAVE_VISIBILITY_KEY, visibility)
            putParcelable(SAVE_SUPER_STATE_KEY, super.onSaveInstanceState())
        }
    }

    override fun onDetachedFromWindow() {
        removeAllViews()
        visibility = GONE

        if (::config.isInitialized && config.useDefaultHandler) {
            defaultEventHandler.release()
        }

        currentPollPayload = null
        pollView = null
        pollResultsView = null

        hideResultsViewJob?.cancel()
        hideResultsViewJob = null

        defaultEventHandler.destroyValues()

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
        private const val SAVE_SUPER_STATE_KEY = "SAVE_SUPER_STATE_KEY"
    }


}