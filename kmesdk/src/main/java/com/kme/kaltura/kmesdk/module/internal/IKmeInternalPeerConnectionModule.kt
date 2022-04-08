package com.kme.kaltura.kmesdk.module.internal

import com.kme.kaltura.kmesdk.module.IKmeModule
import com.kme.kaltura.kmesdk.module.IKmePeerConnectionModule

/**
 * An interface for wrap actions with [IKmePeerConnectionModule]
 * Only for internal SDK usage. This API not visible to the app level.
 */
internal interface IKmeInternalPeerConnectionModule : IKmePeerConnectionModule {

    /**
     * Mute viewers when media content presented in the room
     *
     * @param isEnable flag to enable/disable audio
     */
    fun enableViewersAudioInternal(isEnable: Boolean)

}
