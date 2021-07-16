package com.kme.kaltura.kmesdk.content.playkit

import android.media.AudioManager
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePrefsKeys
import com.kme.kaltura.kmesdk.toNonNull
import com.kme.kaltura.kmesdk.webrtc.audio.IKmeAudioManager
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserType
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

class KmeMediaContentViewModel(
    private val roomController: IKmeRoomController,
    private val userController: IKmeUserController,
    private val audioManager: IKmeAudioManager,
    private val prefs: IKmePreferences
) : ViewModel() {

    fun getCookie(): String = prefs.getString(KmePrefsKeys.COOKIE).toNonNull()

    fun userType(): KmeUserType? = userController.getCurrentParticipant()?.userType

    fun enabledControls(): Boolean = userController.getCurrentParticipant()
        ?.userPermissions?.playlistModule?.defaultSettings?.isModerator == KmePermissionValue.ON

    fun getKalturaPartnerId(): Int {
        val partnerId = roomController.roomSettings?.roomInfo?.integrations?.kaltura?.company?.id
        return partnerId?.toInt() ?: 0
    }

    fun videoVolumeIncrease(){
        audioManager.adjustStreamVolumeRise()
    }

    fun videoVolumeDecrease(){
        audioManager.adjustStreamVolumeLow()
    }
}
