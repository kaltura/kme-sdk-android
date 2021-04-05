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
) : ViewModel(), IKmeDesktopShareModule.KmeDesktopShareEvents {

    private val isDesktopShareActive = MutableLiveData<Boolean>()
    val isDesktopShareActiveLiveData get() = isDesktopShareActive as LiveData<Boolean>

    private val isDesktopShareAvailable = MutableLiveData<Nothing>()
    val isDesktopShareAvailableLiveData get() = isDesktopShareAvailable as LiveData<Nothing>

    private val startView = MutableLiveData<String>()
    val startViewLiveData get() = startView as LiveData<String>

    private val desktopShareHDQuality = MutableLiveData<Boolean>()
    val desktopShareHDQualityLiveData get() = desktopShareHDQuality as LiveData<Boolean>

    private var streamerId: String? = null

    fun listenDesktopShare(renderer: KmeSurfaceRendererView) {
        roomController.desktopShareModule.startListenDesktopShare(renderer, this)
    }

    override fun onDesktopShareActive(isActive: Boolean) {
        isDesktopShareActive.value = isActive
    }

    override fun onDesktopShareAvailable() {
        isDesktopShareAvailable.value = null
    }

    override fun onDesktopShareQualityChanged(isHd: Boolean) {
        desktopShareHDQuality.value = isHd
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
        roomController.desktopShareModule.stopListenDesktopShare()
    }

}
