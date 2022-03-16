package com.kme.kaltura.kmesdk.controller.room.internal

import com.kme.kaltura.kmesdk.controller.room.IKmeRoomModule

/**
 * An interface for room actions
 */
interface IKmeRoomInternalModule : IKmeRoomModule {

    /**
     * Destroy room data
     */
    fun destroy()

}
