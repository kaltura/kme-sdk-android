package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaStateType

/**
 * An interface for actions with participants
 */
interface IKmeParticipantModule {

    /**
     * Raise hand for participant
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param userId id of an interactor
     * @param targetUserId id of a target participant
     * @param isRaise raise hand flag
     */
    fun raiseHand(
        roomId: Long,
        companyId: Long,
        userId: Long,
        targetUserId: Long,
        isRaise: Boolean
    )

    /**
     * Put all hands down in the room
     *
     * @param roomId id of a room
     * @param companyId id of a company
     */
    fun allHandsDown(
        roomId: Long,
        companyId: Long
    )

    /**
     * Changes media state of participant in the room
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param userId id of an interactor
     * @param mediaStateType type of media device
     * @param stateValue media device state
     */
    fun changeMediaState(
        roomId: Long,
        companyId: Long,
        userId: Long,
        mediaStateType: KmeMediaStateType,
        stateValue: KmeMediaDeviceState
    )

}
