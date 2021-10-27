package com.kme.kaltura.kmesdk.content.playkit

import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.livedata.LiveEvent
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeVideoModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeVideoModuleMessage.SyncPlayerStatePayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeVideoModuleMessage.VideoPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmePlayerState

class KmeDefaultPlayerEventHandler : KmeKoinComponent {

    private val roomController: IKmeRoomController by scopedInject()

    private val syncPlayerState = LiveEvent<Pair<KmePlayerState?, Float>>()
    val syncPlayerStateLiveData get() = syncPlayerState

    fun subscribe() {
        roomController.listen(
            playerStateHandler,
            KmeMessageEvent.SYNC_PLAYER_STATE,
            KmeMessageEvent.PLAYER_PLAYING,
            KmeMessageEvent.PLAYER_PAUSED,
            KmeMessageEvent.PLAYER_SEEK_TO
        )
    }

    private val playerStateHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.SYNC_PLAYER_STATE -> {
                    val msg: KmeVideoModuleMessage<SyncPlayerStatePayload>? = message.toType()
                    msg?.payload?.let {
                        syncPlayerState.value = Pair(it.playerState, it.time?.toFloat() ?: 0f)
                    }
                }
                KmeMessageEvent.PLAYER_PLAYING -> {
                    val msg: KmeVideoModuleMessage<VideoPayload>? = message.toType()
                    msg?.payload?.let {
                        syncPlayerState.value = Pair(KmePlayerState.PLAY, it.time?.toFloat() ?: 0f)
                    }
                }
                KmeMessageEvent.PLAYER_PAUSED -> {
                    val msg: KmeVideoModuleMessage<VideoPayload>? = message.toType()
                    msg?.payload?.let {
                        syncPlayerState.value = Pair(KmePlayerState.PAUSE, it.time?.toFloat() ?: 0f)
                    }
                }
                KmeMessageEvent.PLAYER_SEEK_TO -> {
                    val msg: KmeVideoModuleMessage<VideoPayload>? = message.toType()
                    msg?.payload?.let {
                        syncPlayerState.value =
                            Pair(KmePlayerState.SEEK_TO, it.time?.toFloat() ?: 0f)
                    }
                }
            }
        }
    }

    fun setState(state: Pair<KmePlayerState?, Float>) {
        syncPlayerState.value = Pair(state.first, state.second)
    }

    fun release() {
        roomController.remove(playerStateHandler)
    }

}