package com.kme.kaltura.kmesdk.module

import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaStateType
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

/**
 * An interface for actions with participants
 */
interface IKmeParticipantModule : IKmeModule {

    /**
     * Setting initialization data to the module
     *
     * @param listener callback with [KmeParticipantListener] for indicating main events
     */
    fun setListener(listener: KmeParticipantListener)

    /**
     * Get participants list
     *
     * @param roomId id of a room
     *
     * @return list of all participants in case [roomId] = null
     */
    fun getParticipants(roomId: Long? = null): List<KmeParticipant>

    /**
     * Get participant with userId from the list
     *
     * @param userId id of an interactor
     *
     * @return participant from list
     */
    fun getParticipant(userId: Long?): KmeParticipant?

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

    /**
     * Removes participant from the room
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param userId id of moderator
     * @param targetId id of a target participant
     */
    fun remove(
        roomId: Long,
        companyId: Long,
        userId: Long,
        targetId: Long
    )

    /**
     * User live media state
     *
     * @param userId id of a user
     */
    fun userLive(userId: Long)

    /**
     * Mute all users mics
     *
     * @param value value of a permission
     */
    fun muteAllMics(value: KmePermissionValue)

    /**
     * Mute all users cams
     *
     * @param value value of a permission
     */
    fun muteAllCams(value: KmePermissionValue)

    /**
     * Participant listener
     */
    interface KmeParticipantListener {

        /**
         * Callback fired once participants are loaded from the server
         *
         * @param participants list
         */
        fun onParticipantsLoaded(participants: List<KmeParticipant>)

        /**
         * Callback fired when participant updated
         *
         * @param participant
         */
        fun onParticipantChanged(participant: KmeParticipant)

        /**
         * Callback fired when media state payload of Participant changed
         *
         * @param payload
         */
//        fun onParticipantMediaStatePayLoadChanged(payload: UserMediaStateChangedPayload)

        /**
         * Callback fired when medea state changed
         *
         * @param userId for detect who is muted
         * @param mediaStateType for change media state
         * @param stateValue for detect media device state
         */
        fun onParticipantMediaStateChanged(
            userId: Long,
            mediaStateType: KmeMediaStateType?,
            stateValue: KmeMediaDeviceState?
        )

        /**
         * Callback fired once user raise state changes
         *
         * @param targetUserId for a user
         * @param isRaise for a turn on/off
         */
        fun onUserHandRaised(
            targetUserId: Long,
            isRaise: Boolean
        )

        /**
         * Callback fired once when all users hand is down
         */
        fun onUpdateAllHandsDown()

        /**
         * Callback fired once when participant removed
         *
         * @param userId for a user
         * @param isRemoved for checking if user already removed
         */
        fun onParticipantRemoved(
            userId: Long,
            isRemoved: Boolean
        )

        /**
         * Callback fired once dial participant added
         *
         * @param participant
         */
        fun onDialAdded(participant: KmeParticipant)

    }

}
