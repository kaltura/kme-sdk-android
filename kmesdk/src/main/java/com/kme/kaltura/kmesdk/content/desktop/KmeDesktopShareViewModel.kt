package com.kme.kaltura.kmesdk.content.desktop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.room.IKmeDesktopShareModule
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType

internal class KmeDesktopShareViewModel(
    private val userController: IKmeUserController,
    private val roomController: IKmeRoomController
) : ViewModel(), IKmeDesktopShareModule.KmeDesktopShareEvents {

    private val isAdmin = MutableLiveData<Boolean>()
    val isAdminLiveData get() = isAdmin as LiveData<Boolean>

    private val isDesktopShareActive = MutableLiveData<Pair<Boolean, Boolean>>()
    val isDesktopShareActiveLiveData get() = isDesktopShareActive as LiveData<Pair<Boolean, Boolean>>

    private val isDesktopShareAvailable = MutableLiveData<Nothing>()
    val isDesktopShareAvailableLiveData get() = isDesktopShareAvailable as LiveData<Nothing>

    private val desktopShareHDQuality = MutableLiveData<Boolean>()
    val desktopShareHDQualityLiveData get() = desktopShareHDQuality as LiveData<Boolean>

    fun listenDesktopShare(renderer: KmeSurfaceRendererView) {
        isAdmin.value = userController.isModerator()
        roomController.desktopShareModule.startListenDesktopShare(renderer, this)
    }

    fun askForScreenSharePermission() {
        roomController.peerConnectionModule.askForScreenSharePermission()
    }

    fun stopScreenShare() {
        roomController.peerConnectionModule.stopScreenShare()
    }

    fun setConferenceView() {
        roomController.roomModule.setActiveContent(KmeContentType.CONFERENCE_VIEW)
    }

    override fun onDesktopShareActive(isActive: Boolean, isYour: Boolean) {
        isDesktopShareActive.value = Pair(isActive, isYour)
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
