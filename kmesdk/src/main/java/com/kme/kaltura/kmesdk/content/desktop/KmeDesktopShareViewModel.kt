package com.kme.kaltura.kmesdk.content.desktop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.room.internal.IKmeInternalDataModule
import com.kme.kaltura.kmesdk.di.KmeKoinViewModel
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.livedata.LiveEvent
import com.kme.kaltura.kmesdk.util.messages.buildDesktopShareInitOnRoomInitMessage
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.KmeMessagePriority
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType
import org.koin.core.inject

internal class KmeDesktopShareViewModel : ViewModel(), KmeKoinViewModel {

    private val userController: IKmeUserController by inject()
    private val roomController: IKmeRoomController by scopedInject()
    private val internalDataModule: IKmeInternalDataModule by inject()

    private val isAdmin = LiveEvent<Boolean>()
    val isAdminLiveData get() = isAdmin

    private val isDesktopShareActive = MutableLiveData<Pair<Boolean, Boolean>>()
    val isDesktopShareActiveLiveData get() = isDesktopShareActive as LiveData<Pair<Boolean, Boolean>>

    private val isDesktopShareAvailable = LiveEvent<Boolean>()
    val isDesktopShareAvailableLiveData get() = isDesktopShareAvailable

    private val desktopShareHDQuality = LiveEvent<Boolean>()
    val desktopShareHDQualityLiveData get() = desktopShareHDQuality

    private val publisherId: String by lazy {
        userController.getCurrentUserInfo()?.getUserId().toString()
    }

    private var requestedUserIdStream: String? = null

    init {
        isAdmin.value = userController.isModerator()
                || userController.isAdminFor(internalDataModule.companyId)
    }

    /**
     * Start listen desktop share events
     */
    fun subscribe() {
        roomController.listen(
            desktopShareHandler,
            KmeMessageEvent.DESKTOP_SHARE_STATE_UPDATED,
            KmeMessageEvent.USER_STARTED_TO_PUBLISH,
            KmeMessageEvent.SDP_OFFER_FOR_VIEWER,
            KmeMessageEvent.DESKTOP_SHARE_QUALITY_UPDATED,
            priority = KmeMessagePriority.NORMAL
        )
        roomController.send(buildDesktopShareInitOnRoomInitMessage())
    }

    /**
     * Handler for WS desktop share events
     */
    private val desktopShareHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.DESKTOP_SHARE_STATE_UPDATED -> {
                    val msg: KmeDesktopShareModuleMessage<KmeDesktopShareModuleMessage.DesktopShareStateUpdatedPayload>? =
                        message.toType()

                    msg?.payload?.isActive?.let { isActive ->
                        val isYour = msg.payload?.userId?.toString().equals(publisherId)
                        isDesktopShareActive.value = Pair(isActive, isYour)

                        if (isActive) {
                            val onRoomInit = msg.payload?.onRoomInit ?: false
                            if (onRoomInit) {
                                onStreamReadyToView("${msg.payload?.userId}_desk")
                            }
                        } else {
                            stopView()
                            stopScreenShare()
                        }
                    }
                }
                KmeMessageEvent.USER_STARTED_TO_PUBLISH -> {
                    val msg: KmeStreamingModuleMessage<KmeStreamingModuleMessage.StartedPublishPayload>? =
                        message.toType()

                    msg?.payload?.userId?.let {
                        if (it.toLongOrNull() == null && it.contains("_desk")) {
                            onStreamReadyToView(it)
                        }
                    }
                }
                KmeMessageEvent.DESKTOP_SHARE_QUALITY_UPDATED -> {
                    val msg: KmeDesktopShareModuleMessage<KmeDesktopShareModuleMessage.DesktopShareQualityUpdatedPayload>? =
                        message.toType()

                    msg?.payload?.isHD?.let {
                        desktopShareHDQuality.value = it
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun onStreamReadyToView(requestedUserIdStream: String) {
        if (requestedUserIdStream.contains(publisherId)) {
            return
        }
        this.requestedUserIdStream = requestedUserIdStream
        roomController.peerConnectionModule.addViewerConnection(requestedUserIdStream)
        isDesktopShareAvailable.value = true
    }

    // Viewer actions

    fun setViewerRenderer(renderer: KmeSurfaceRendererView) {
        requestedUserIdStream?.let {
            roomController.peerConnectionModule.setViewerRenderer(it, renderer)
        }
    }

    fun stopView() {
        requestedUserIdStream?.let {
            requestedUserIdStream = null
            roomController.peerConnectionModule.disconnect(it)
        }
    }

    // Admin actions

    fun setConferenceView() {
        roomController.roomModule.setActiveContent(KmeContentType.CONFERENCE_VIEW)
    }

    fun askForScreenSharePermission() {
        roomController.peerConnectionModule.askForScreenSharePermission()
    }

    fun setScreenShareRenderer(renderer: KmeSurfaceRendererView) {
        roomController.peerConnectionModule.setScreenShareRenderer(renderer)
    }

    fun stopScreenShare() {
        roomController.peerConnectionModule.stopScreenShare()
    }

    fun updateModeratorState(isModerator: Boolean): Boolean {
        if (isAdmin.value != isModerator) {
            isAdmin.value = isModerator
            return true
        }
        return false
    }

    override fun onCleared() {
        super.onCleared()
        roomController.removeListener(desktopShareHandler)
        stopView()
        stopScreenShare()
    }

    override fun onClosed() {
        onCleared()
    }

}
