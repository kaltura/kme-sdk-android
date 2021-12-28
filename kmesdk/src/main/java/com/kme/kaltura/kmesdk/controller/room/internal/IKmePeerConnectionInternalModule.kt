package com.kme.kaltura.kmesdk.controller.room.internal

import com.kme.kaltura.kmesdk.controller.room.IKmeModule
import com.kme.kaltura.kmesdk.controller.room.IKmePeerConnectionModule
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnection

/**
 * An interface for wrap actions with [IKmePeerConnection]
 * Only for internal SDK usage. This API not visible to the app level.
 */
interface IKmePeerConnectionInternalModule : IKmePeerConnectionModule, IKmeModule {

    /**
     * Mute viewers when media content presented in the room
     *
     * @param isEnable flag to enable/disable audio
     */
    fun enableViewersAudioInternal(isEnable: Boolean)

}