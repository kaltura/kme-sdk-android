package com.kme.kaltura.kmesdk.content.playkit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeVideoModuleMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmePlayerState

class KmeDefaultPlayerEventHandler(
    private val roomController: IKmeRoomController
) {

    private val syncPlayerState = MutableLiveData<Pair<KmePlayerState?, Float>>()
    val syncPlayerStateLiveData
        get() = syncPlayerState as LiveData<Pair<KmePlayerState?, Float>>

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
                    val contentMessage: KmeVideoModuleMessage<KmeVideoModuleMessage.SyncPlayerStatePayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        syncPlayerState.postValue(
                            Pair(it.playerState, it.time?.toFloat() ?: 0f)
                        )
                    }
                }
                KmeMessageEvent.PLAYER_PLAYING -> {
                    val contentMessage: KmeVideoModuleMessage<KmeVideoModuleMessage.VideoPayload>? =
                        message.toType()
                    contentMessage?.payload?.let {
                        syncPlayerState.postValue(
                            Pair(KmePlayerState.PLAY, it.time?.toFloat() ?: 0f)
                        )
                    }
                }
                KmeMessageEvent.PLAYER_PAUSED -> {
                    val contentMessage: KmeVideoModuleMessage<KmeVideoModuleMessage.VideoPayload>? =
                        message.toType()
                    contentMessage?.payload?.let {
                        syncPlayerState.postValue(
                            Pair(KmePlayerState.PAUSE, it.time?.toFloat() ?: 0f)
                        )
                    }
                }
                KmeMessageEvent.PLAYER_SEEK_TO -> {
                    val contentMessage: KmeVideoModuleMessage<KmeVideoModuleMessage.VideoPayload>? =
                        message.toType()
                    contentMessage?.payload?.let {
                        syncPlayerState.postValue(
                            Pair(KmePlayerState.SEEK_TO, it.time?.toFloat() ?: 0f)
                        )
                    }
                }
            }
        }
    }

    fun release() {
        roomController.remove(playerStateHandler)
    }

}