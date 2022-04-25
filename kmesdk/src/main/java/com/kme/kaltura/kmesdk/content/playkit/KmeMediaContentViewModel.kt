package com.kme.kaltura.kmesdk.content.playkit

import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.controller.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.di.KmeKoinViewModel
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.module.internal.IKmeInternalDataModule
import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePrefsKeys
import com.kme.kaltura.kmesdk.toNonNull
import com.kme.kaltura.kmesdk.util.messages.buildGetPlayerStateMessage
import com.kme.kaltura.kmesdk.webrtc.audio.IKmeAudioManager
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType
import com.kme.kaltura.kmesdk.ws.message.type.KmePlayerState
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserType
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import org.koin.core.inject

class KmeMediaContentViewModel : ViewModel(), KmeKoinViewModel {

    private val userController: IKmeUserController by inject()
    private val prefs: IKmePreferences by inject()
    private val audioManager: IKmeAudioManager by inject()
    private val internalDataModule: IKmeInternalDataModule by inject()
    private val roomController: IKmeRoomController by scopedInject()

    var isMute = false

    fun getCookie(): String = prefs.getString(KmePrefsKeys.COOKIE).toNonNull()

    fun userType(): KmeUserType? = userController.getCurrentParticipant()?.userType

    fun enabledControls(): Boolean = userController.getCurrentParticipant()
        ?.userPermissions?.playlistModule?.defaultSettings?.isModerator == KmePermissionValue.ON

    fun getKalturaPartnerId(): Int {
        val partnerId = roomController.webRTCServer?.roomInfo?.integrations?.kaltura?.company?.id
        return partnerId?.toInt() ?: 0
    }

    fun reportPlayerStateChange(
        state: KmePlayerState,
        type: KmeContentType
    ) = roomController.peerConnectionModule.reportPlayerStateChange(state, type)

    fun videoVolumeIncrease() = audioManager.adjustStreamVolumeRise()

    fun videoVolumeDecrease() = audioManager.adjustStreamVolumeLow()

    fun getPlayerState(contentType: KmeContentType?) {
        val module: KmeMessageModule = when (contentType) {
            KmeContentType.KALTURA -> KmeMessageModule.KALTURA
            KmeContentType.VIDEO -> KmeMessageModule.VIDEO
            KmeContentType.AUDIO -> KmeMessageModule.AUDIO
            KmeContentType.YOUTUBE -> KmeMessageModule.YOUTUBE
            else -> null
        } ?: return

        val roomId = internalDataModule.breakoutRoomId.takeIf {
            it != 0L
        } ?: internalDataModule.mainRoomId
        roomController.send(
            buildGetPlayerStateMessage(
                roomId,
                internalDataModule.companyId,
                module
            )
        )
    }

    override fun onClosed() {
        onCleared()
    }

}
