package com.kme.kaltura.kmesdk.content.playkit

import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.room.internal.IKmeInternalPeerConnectionModule
import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.livedata.LiveEvent
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.KmeMessagePriority
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeVideoModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeVideoModuleMessage.SyncPlayerStatePayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeVideoModuleMessage.VideoPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmePlayerState
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

class KmeDefaultPlayerEventHandler : KmeKoinComponent {

    private val roomController: IKmeRoomController by scopedInject()
    private val peerConnectionModule: IKmeInternalPeerConnectionModule by scopedInject()

    private val syncPlayerState = LiveEvent<Pair<KmePlayerState?, Float>>()
    val syncPlayerStateLiveData get() = syncPlayerState

    fun subscribe() {
        roomController.listen(
            playerStateHandler,
            KmeMessageEvent.SYNC_PLAYER_STATE,
            KmeMessageEvent.PLAYER_PLAYING,
            KmeMessageEvent.PLAYER_PAUSED,
            KmeMessageEvent.PLAYER_SEEK_TO,
            priority = KmeMessagePriority.NORMAL
        )
    }

    private val playerStateHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.SYNC_PLAYER_STATE -> {
                    val msg: KmeVideoModuleMessage<SyncPlayerStatePayload>? = message.toType()
                    msg?.payload?.let {
                        syncPlayerState.value = Pair(it.playerState, it.time?.toFloat() ?: 0f)
                        when (it.playerState) {
                            KmePlayerState.STOP,
                            KmePlayerState.PAUSE,
                            KmePlayerState.ENDED,
                            KmePlayerState.PAUSED -> enableViewersAudio(true)
                            else -> enableViewersAudio(false)
                        }
                    }
                }
                KmeMessageEvent.PLAYER_PLAYING -> {
                    val msg: KmeVideoModuleMessage<VideoPayload>? = message.toType()
                    msg?.payload?.let {
                        syncPlayerState.value = Pair(KmePlayerState.PLAY, it.time?.toFloat() ?: 0f)
                        enableViewersAudio(false)
                    }
                }
                KmeMessageEvent.PLAYER_PAUSED -> {
                    val msg: KmeVideoModuleMessage<VideoPayload>? = message.toType()
                    msg?.payload?.let {
                        syncPlayerState.value = Pair(KmePlayerState.PAUSE, it.time?.toFloat() ?: 0f)
                        enableViewersAudio(true)
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

    private fun enableViewersAudio(isEnable: Boolean) =
        roomController.webRTCServer?.roomInfo?.settingsV2?.general?.muteOnPlay?.let {
            val enabled = if (it == KmePermissionValue.ON) isEnable else true
            peerConnectionModule.enableViewersAudioInternal(enabled)
        }

    fun setState(state: Pair<KmePlayerState?, Float>) {
        syncPlayerState.value = Pair(state.first, state.second)
    }

    fun release() {
        roomController.remove(playerStateHandler)
    }

}