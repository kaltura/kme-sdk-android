package com.kme.kaltura.kmeapplication.viewmodel.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmeapplication.util.extensions.toNonNull
import com.kme.kaltura.kmeapplication.view.view.content.controls.PlayerControlsEvent
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.SetActiveContentPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeSlidesPlayerModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeSlidesPlayerModuleMessage.SlideChangedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeVideoModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeVideoModuleMessage.SyncPlayerStatePayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeVideoModuleMessage.VideoPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmePlayerState
import com.kme.kaltura.kmesdk.ws.message.type.KmePlayerState.*
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

class ActiveContentViewModel(
    private val kmeSdk: KME
) : ViewModel() {

    private val setActiveContent =
        MutableLiveData<SetActiveContentPayload>()
    val setActiveContentLiveData
        get() = setActiveContent as LiveData<SetActiveContentPayload>

    private val syncPlayerState =
        MutableLiveData<Pair<PlayerControlsEvent?, Float>>()
    val syncPlayerStateLiveData
        get() = syncPlayerState as LiveData<Pair<PlayerControlsEvent?, Float>>

    private val slideChanged =
        MutableLiveData<Int>()
    val slideChangedLiveData
        get() = slideChanged as LiveData<Int>

    fun subscribe() {
        kmeSdk.roomController.listen(
            activeContentHandler,
            KmeMessageEvent.INIT_ACTIVE_CONTENT,
            KmeMessageEvent.SET_ACTIVE_CONTENT
        )

        kmeSdk.roomController.listen(
            playerStateHandler,
            KmeMessageEvent.SYNC_PLAYER_STATE,
            KmeMessageEvent.PLAYER_PLAYING,
            KmeMessageEvent.PLAYER_PAUSED,
            KmeMessageEvent.PLAYER_SEEK_TO
        )

        kmeSdk.roomController.listen(
            slidePlayerHandler,
            KmeMessageEvent.SLIDE_CHANGED
        )
    }

    fun getCookie(): String = kmeSdk.getCookies().toNonNull()

    fun getFilesUrl(): String = kmeSdk.getFilesUrl().toNonNull()

    fun enabledControls(): Boolean = kmeSdk.userController.getCurrentParticipant()
        ?.userPermissions?.playlistModule?.defaultSettings?.isModerator == KmePermissionValue.ON

    private val activeContentHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.INIT_ACTIVE_CONTENT -> {
                    val contentMessage: KmeActiveContentModuleMessage<SetActiveContentPayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        setActiveContent.postValue(it)
                    }
                }
                KmeMessageEvent.SET_ACTIVE_CONTENT -> {
                    val contentMessage: KmeActiveContentModuleMessage<SetActiveContentPayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        setActiveContent.postValue(it)
                    }
                }
            }
        }
    }

    private val playerStateHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.SYNC_PLAYER_STATE -> {
                    val contentMessage: KmeVideoModuleMessage<SyncPlayerStatePayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        syncPlayerState.postValue(
                            Pair(it.playerState?.toControlsEvent(), it.time?.toFloat() ?: 0f)
                        )
                    }
                }
                KmeMessageEvent.PLAYER_PLAYING -> {
                    val contentMessage: KmeVideoModuleMessage<VideoPayload>? = message.toType()
                    contentMessage?.payload?.let {
                        syncPlayerState.postValue(
                            Pair(PlayerControlsEvent.PLAY, it.time?.toFloat() ?: 0f)
                        )
                    }
                }
                KmeMessageEvent.PLAYER_PAUSED -> {
                    val contentMessage: KmeVideoModuleMessage<VideoPayload>? = message.toType()
                    contentMessage?.payload?.let {
                        syncPlayerState.postValue(
                            Pair(PlayerControlsEvent.PAUSE, it.time?.toFloat() ?: 0f)
                        )
                    }
                }
                KmeMessageEvent.PLAYER_SEEK_TO -> {
                    val contentMessage: KmeVideoModuleMessage<VideoPayload>? = message.toType()
                    contentMessage?.payload?.let {
                        syncPlayerState.postValue(
                            Pair(PlayerControlsEvent.SEEK_TO, it.time?.toFloat() ?: 0f)
                        )
                    }
                }
            }
        }
    }

    private val slidePlayerHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.SLIDE_CHANGED -> {
                    val msg: KmeSlidesPlayerModuleMessage<SlideChangedPayload>? = message.toType()
                    msg?.payload?.let {
                        slideChanged.postValue(it.nextActiveSlide)
                    }
                }
            }
        }
    }

    private fun KmePlayerState.toControlsEvent(): PlayerControlsEvent {
        return when (this) {
            PLAY -> PlayerControlsEvent.PLAY
            SEEK_TO -> PlayerControlsEvent.SEEK_TO
            PLAYING -> PlayerControlsEvent.PLAYING
            PAUSED, ENDED, PAUSE, STOP -> PlayerControlsEvent.PAUSE
        }
    }

}
