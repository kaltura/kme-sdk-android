package com.kme.kaltura.kmesdk.controller.room.internal

import com.kme.kaltura.kmesdk.controller.room.IKmeModule
import com.kme.kaltura.kmesdk.controller.room.IKmeParticipantModule

/**
 * An interface for wrap actions with [IKmeParticipantModule]
 * Only for internal SDK usage. This API not visible to the app level.
 */
internal interface IKmeInternalParticipantModule : IKmeParticipantModule, IKmeModule {

    /**
     * Subscribing for the room events related to participants
     */
    fun subscribe()

    /*
    * Clear participants list
    * */
    fun clearParticipants()

}
