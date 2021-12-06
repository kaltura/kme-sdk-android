package com.kme.kaltura.kmesdk.content.poll

import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.livedata.LiveEvent
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage.*

class KmeDefaultPollEventHandler : KmeKoinComponent {

    private val roomController: IKmeRoomController by scopedInject()

    private val pollStarted = LiveEvent<QuickPollStartedPayload>()
    val pollStartedLiveData
        get() = pollStarted

    private val pollEnded = LiveEvent<QuickPollEndedPayload>()
    val pollEndedLiveData
        get() = pollEnded

    private val userAnsweredPoll = LiveEvent<QuickPollUserAnsweredPayload>()
    val userAnsweredPollLiveData
        get() = userAnsweredPoll

    fun subscribe() {
        roomController.listen(
            quickPollHandler,
            KmeMessageEvent.MODULE_STATE,
            KmeMessageEvent.QUICK_POLL_STARTED,
            KmeMessageEvent.QUICK_POLL_ENDED,
            KmeMessageEvent.QUICK_POLL_ANSWERS,
            KmeMessageEvent.QUICK_POLL_USER_ANSWERED
        )
    }

    private val quickPollHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.MODULE_STATE -> {
                    val msg: KmeQuickPollModuleMessage<QuickPollGetStatePayload>? = message.toType()
                }
                KmeMessageEvent.QUICK_POLL_STARTED -> {
                    val msg: KmeQuickPollModuleMessage<QuickPollStartedPayload>? = message.toType()
                    msg?.payload?.let {
                        pollEnded.postValue(null)
                        pollStarted.postValue(it)
                    }
                }
                KmeMessageEvent.QUICK_POLL_ENDED -> {
                    val msg: KmeQuickPollModuleMessage<QuickPollEndedPayload>? = message.toType()
                    msg?.payload?.let {
                        pollEnded.postValue(it)
                    }
                }
                KmeMessageEvent.QUICK_POLL_ANSWERS -> {
                }
                KmeMessageEvent.QUICK_POLL_USER_ANSWERED -> {
                    val msg: KmeQuickPollModuleMessage<QuickPollUserAnsweredPayload>? =
                        message.toType()
                    msg?.payload?.let {
                        userAnsweredPoll.postValue(it)
                    }
                }
            }
        }
    }

    fun release() {
        roomController.remove(quickPollHandler)
    }

    fun destroyValues() {
        pollEnded.postValue(null)
        pollStarted.postValue(null)
        userAnsweredPoll.postValue(null)
    }
}