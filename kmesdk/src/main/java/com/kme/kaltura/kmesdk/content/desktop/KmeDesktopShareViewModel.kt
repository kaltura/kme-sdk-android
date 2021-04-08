package com.kme.kaltura.kmesdk.content.desktop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.controller.room.IKmeDesktopShareModule
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView

class KmeDesktopShareViewModel(
    private val roomController: IKmeRoomController
) : ViewModel(), IKmeDesktopShareModule.KmeDesktopShareEvents {

    private val isDesktopShareActive = MutableLiveData<Boolean>()
    val isDesktopShareActiveLiveData get() = isDesktopShareActive as LiveData<Boolean>

    private val isDesktopShareAvailable = MutableLiveData<Nothing>()
    val isDesktopShareAvailableLiveData get() = isDesktopShareAvailable as LiveData<Nothing>

    private val desktopShareHDQuality = MutableLiveData<Boolean>()
    val desktopShareHDQualityLiveData get() = desktopShareHDQuality as LiveData<Boolean>

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

    fun stopView() {
        roomController.desktopShareModule.stopListenDesktopShare()
    }

    override fun onCleared() {
        super.onCleared()
        roomController.desktopShareModule.stopListenDesktopShare()
    }

}
