package com.kme.kaltura.kmesdk.content.poll

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmePlayerState

class KmeDefaultPollEventHandler(
    private val roomController: IKmeRoomController
) {

    private val pollStarted = MutableLiveData<QuickPollStartedPayload>()
    val pollStartedLiveData
        get() = pollStarted as LiveData<QuickPollStartedPayload>

    private val pollEnded = MutableLiveData<QuickPollEndedPayload>()
    val pollEndedLiveData
        get() = pollEnded as LiveData<QuickPollEndedPayload>

    private val userAnsweredPoll = MutableLiveData<QuickPollUserAnsweredPayload>()
    val userAnsweredPollLiveData
        get() = userAnsweredPoll as LiveData<QuickPollUserAnsweredPayload>

    fun subscribe() {
        roomController.listen(
            quickPollHandler,
            KmeMessageEvent.QUICK_POLL_STARTED,
            KmeMessageEvent.QUICK_POLL_ENDED,
            KmeMessageEvent.QUICK_POLL_ANSWERS,
            KmeMessageEvent.QUICK_POLL_STATE,
            KmeMessageEvent.QUICK_POLL_USER_ANSWERED
        )
    }

    private val quickPollHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.QUICK_POLL_STARTED -> {
                    val msg: KmeQuickPollModuleMessage<QuickPollStartedPayload>? = message.toType()
                    msg?.payload?.let {
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
                KmeMessageEvent.QUICK_POLL_STATE -> {
                }
                KmeMessageEvent.QUICK_POLL_USER_ANSWERED -> {
                    val msg: KmeQuickPollModuleMessage<QuickPollUserAnsweredPayload>? = message.toType()
                    msg?.payload?.let {
                        userAnsweredPoll.postValue(it)
                    }
                }
            }
        }
    }

    fun release() {
        roomController.remove(quickPollHandler)
        pollStarted.value = null
        pollEnded.value = null
    }

}