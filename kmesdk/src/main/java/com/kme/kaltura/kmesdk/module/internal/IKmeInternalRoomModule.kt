package com.kme.kaltura.kmesdk.module.internal

import com.kme.kaltura.kmesdk.module.IKmeRoomModule

/**
 * An interface for room actions
 */
interface IKmeInternalRoomModule : IKmeRoomModule {

    /**
     * Destroy room data
     */
    fun destroy()

}
