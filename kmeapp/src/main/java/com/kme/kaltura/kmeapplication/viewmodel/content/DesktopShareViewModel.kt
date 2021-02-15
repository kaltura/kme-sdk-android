package com.kme.kaltura.kmeapplication.viewmodel.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.controller.room.IKmeDesktopShareModule
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView

class DesktopShareViewModel(
    private val kmeSdk: KME
) : ViewModel(), IKmeDesktopShareModule.KmeDesktopShareEvents {

    private val isDesktopShareActive = MutableLiveData<Boolean>()
    val isDesktopShareActiveLiveData get() = isDesktopShareActive as LiveData<Boolean>

    private val desktopShareHDQuality = MutableLiveData<Boolean>()
    val desktopShareHDQualityLiveData get() = desktopShareHDQuality as LiveData<Boolean>

    private var roomId: Long = 0
    private var companyId: Long = 0

    fun setRoomData(companyId: Long, roomId: Long) {
        this.companyId = companyId
        this.roomId = roomId
    }

    fun listenDesktopShare(renderer: KmeSurfaceRendererView) {
        kmeSdk.roomController.desktopShareModule.startListenDesktopShare(
            roomId,
            companyId,
            renderer,
            this
        )
    }

    override fun onDesktopShareActive(isActive: Boolean) {
        isDesktopShareActive.value = isActive
    }

    override fun onDesktopShareQualityChanged(isHd: Boolean) {
        desktopShareHDQuality.value = isHd
    }

    override fun onCleared() {
        super.onCleared()
        kmeSdk.roomController.desktopShareModule.stopListenDesktopShare()
    }

}
