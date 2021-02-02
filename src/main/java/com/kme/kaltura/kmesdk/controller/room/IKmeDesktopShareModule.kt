package com.kme.kaltura.kmesdk.controller.room

/**
 * An interface for desktop share actions
 */
interface IKmeDesktopShareModule {

    /**
     * Listen desktop share event if need
     *
     * @param roomId id of a room
     * @param companyId id of a company
     */
    fun listenDesktopShare(roomId: Long, companyId: Long)

}
