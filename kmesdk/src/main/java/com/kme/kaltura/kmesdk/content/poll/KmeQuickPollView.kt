package com.kme.kaltura.kmesdk.content.poll

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.content.poll.type.KmeQuickPollTypeView
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.util.messages.buildSendQuickPollAnswerMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType
import org.koin.core.inject

class KmeQuickPollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    KmeKoinComponent, IKmeQuickPollView,
    KmeQuickPollTypeView.OnAnswerListener, KmeQuickPollResultsView.OnCloseResultsListener {

    var state: State = State.GONE
        private set

    private lateinit var config: Config

    private val defaultEventHandler: KmeDefaultPollEventHandler by inject()
    private val roomController: IKmeRoomController by inject()

    private var pollView: KmeQuickPollTypeView? = null
    private var pollResultsView: KmeQuickPollResultsView? = null
    private var currentPollPayload: QuickPollStartedPayload? = null

    init {
        visibility = GONE
        setBackgroundColor(ContextCompat.getColor(context, R.color.transparentColor1))
    }

    override fun init(config: Config) {
        this.config = config

        if (config.useDefaultHandler) {
            setupDefaultEventHandler()
        }
    }

    private fun setupDefaultEventHandler() {
        defaultEventHandler.pollStartedLiveData.observeForever { startPoll(it) }
        defaultEventHandler.pollEndedLiveData.observeForever { endPoll(it) }
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
        visibility = VISIBLE
        state = State.WAITING_FOR_ANSWER_VIEW
        removeAllViews()

        currentPollPayload = payload
        pollView = KmeQuickPollTypeView.getView(context, payload.type)?.also {
            it.listener = this
            addView(it)
        }
    }

    override fun endPoll(payload: QuickPollEndedPayload) {
        if (payload.shouldPresent == true) {
            showResults(payload)
        } else {
            visibility = GONE
            state = State.GONE
            removeAllViews()
            pollView = null
            currentPollPayload = null
        }
    }

    override fun onAnswered(type: KmeQuickPollType, answer: Int) {
        sendAnswer(QuickPollPayload.Answer(answer, currentPollPayload?.pollId))
    }

    override fun showResults(payload: QuickPollEndedPayload) {
        removeAllViews()
        visibility = VISIBLE
        state = State.RESULT_VIEW
        pollView = null
        pollResultsView = KmeQuickPollResultsView(context).also {
            it.closeListener = this
            it.init(payload)
            addView(it)
        }
    }

    override fun onCloseResultsView() {
        removeAllViews()
        state = State.GONE
        visibility = GONE
        pollResultsView = null
    }

    override fun onDetachedFromWindow() {
        if (config.useDefaultHandler) {
            defaultEventHandler.release()
        }
        currentPollPayload = null
        pollView = null
        pollResultsView = null
        super.onDetachedFromWindow()
    }

    enum class State {
        GONE, RESULT_VIEW, MODERATOR_RESULT_VIEW, WAITING_FOR_ANSWER_VIEW, WAITING_FOR_RESULT
    }

    class Config {
        var useDefaultHandler = true
    }

}