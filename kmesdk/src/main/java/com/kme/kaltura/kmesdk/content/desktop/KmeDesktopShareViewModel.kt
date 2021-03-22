package com.kme.kaltura.kmesdk.content.desktop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.room.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.messages.buildDesktopShareInitOnRoomInitMessage
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage.DesktopShareQualityUpdatedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage.DesktopShareStateUpdatedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage.StartedPublishPayload

class KmeDesktopShareViewModel(
    private val roomController: IKmeRoomController,
    private val webSocketModule: IKmeWebSocketModule
) : ViewModel() {

    private val isDesktopShareActive = MutableLiveData<Boolean>()
    val isDesktopShareActiveLiveData get() = isDesktopShareActive as LiveData<Boolean>

    private val startView = MutableLiveData<String>()
    val startViewLiveData get() = startView as LiveData<String>

    private val desktopShareHDQuality = MutableLiveData<Boolean>()
    val desktopShareHDQualityLiveData get() = desktopShareHDQuality as LiveData<Boolean>

    private var streamerId: String? = null

    fun listenDesktopShare() {
        roomController.listen(
            desktopShareHandler,
            KmeMessageEvent.DESKTOP_SHARE_STATE_UPDATED,
            KmeMessageEvent.USER_STARTED_TO_PUBLISH,
            KmeMessageEvent.SDP_OFFER_FOR_VIEWER,
            KmeMessageEvent.DESKTOP_SHARE_QUALITY_UPDATED
        )
        webSocketModule.send(buildDesktopShareInitOnRoomInitMessage())
    }

    private val desktopShareHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.DESKTOP_SHARE_STATE_UPDATED -> {
                    val msg: KmeDesktopShareModuleMessage<DesktopShareStateUpdatedPayload>? =
                        message.toType()

                    val isActive = msg?.payload?.isActive ?: false
                    val onRoomInit = msg?.payload?.onRoomInit ?: false

                    isDesktopShareActive.value = isActive

                    if (!isActive) {
                        roomController.peerConnectionModule.disconnect(
                            "${msg?.payload?.userId}_desk")
                    } else if (onRoomInit) {
                        startView.value = "${msg?.payload?.userId}_desk"
                    }
                }
                KmeMessageEvent.USER_STARTED_TO_PUBLISH -> {
                    val msg: KmeStreamingModuleMessage<StartedPublishPayload>? = message.toType()

                    msg?.payload?.userId?.let {
                        if (it.toLongOrNull() == null) {
                            roomController.peerConnectionModule.disconnect(it)
                            startView.value = it
                        }
                    }
                }
                KmeMessageEvent.DESKTOP_SHARE_QUALITY_UPDATED -> {
                    val msg: KmeDesktopShareModuleMessage<DesktopShareQualityUpdatedPayload>? =
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

    fun startView(
        id: String,
        renderer: KmeSurfaceRendererView
    ) {
        streamerId = id
        roomController.peerConnectionModule.addViewer(id, renderer)
    }

    fun stopView() {
        roomController.removeListener(desktopShareHandler)
        streamerId?.let {
            streamerId = null
            roomController.peerConnectionModule.disconnect(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopView()
    }

}
