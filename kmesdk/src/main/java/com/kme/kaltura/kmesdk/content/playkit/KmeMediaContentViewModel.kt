package com.kme.kaltura.kmesdk.content.playkit

import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.di.KmeKoinViewModel
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePrefsKeys
import com.kme.kaltura.kmesdk.toNonNull
import com.kme.kaltura.kmesdk.webrtc.audio.IKmeAudioManager
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserType
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import org.koin.core.inject

class KmeMediaContentViewModel : ViewModel(), KmeKoinViewModel {

    private val userController: IKmeUserController by inject()
    private val prefs: IKmePreferences by inject()
    private val audioManager: IKmeAudioManager by inject()
    private val roomController:  IKmeRoomController by scopedInject()

    var isMute = false

    fun getCookie(): String = prefs.getString(KmePrefsKeys.COOKIE).toNonNull()

    fun userType(): KmeUserType? = userController.getCurrentParticipant()?.userType

    fun enabledControls(): Boolean = userController.getCurrentParticipant()
        ?.userPermissions?.playlistModule?.defaultSettings?.isModerator == KmePermissionValue.ON

    fun getKalturaPartnerId(): Int {
        val partnerId = roomController.webRTCServer?.roomInfo?.integrations?.kaltura?.company?.id
        return partnerId?.toInt() ?: 0
    }

    fun videoVolumeIncrease() {
        audioManager.adjustStreamVolumeRise()
    }

    fun videoVolumeDecrease() {
        audioManager.adjustStreamVolumeLow()
    }

    override fun onClosed() {
        onCleared()
    }

}
