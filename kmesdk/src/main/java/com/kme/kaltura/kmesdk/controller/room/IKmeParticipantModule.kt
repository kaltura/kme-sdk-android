package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage.UserMediaStateChangedPayload
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
    fun init(listener: KmeParticipantListener)

    /**
     * Subscribing for the room events related to participants
     */
    fun subscribe()

    /**
     * Get participants list
     */
    fun participants(): List<KmeParticipant>

    /**
     * Add or update participants list
     *
     * @param userId id of an interactor
     *
     * @return participant from list
     */
    fun getParticipant(userId: Long?): KmeParticipant?

    /**
     * get participant with userId
     *
     * @param participant for add or update list
     *
     * */
    fun addOrUpdateParticipant(participant: KmeParticipant)

    /**
     * initialize user media state
     *
     * @param payload for to get participant media state
     *
     * */
    fun initUserMediaState(payload: KmeParticipantsModuleMessage.UserMediaStateInitPayload)

    /**
     * update user media state
     *
     * @param payload for to update participant media state
     *
     * */
    fun updateUserMediaState(payload: UserMediaStateChangedPayload)

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
     * mute all participant
     *
     * @param initiatorId id of a initiator
     * @param stateType type of a media
     */
    fun updateAllMute(
        initiatorId: Long,
        stateType: KmeMediaStateType
    )

    /**
     * update user live media state
     *
     * @param userId id of a user
     */
    fun updateUserLive(userId: Long)

    /**
     * mute all users mics
     *
     * @param value value of a permission
     */
    fun updateStrongMuteAllMics(value: KmePermissionValue)

    /**
     * mute all users cams
     *
     * @param value value of a permission
     */
    fun updateStrongMuteAllCams(value: KmePermissionValue)

    /**
     * update user raise state
     *
     * @param userId id of a user
     * @param isHandRaised change raise state
     */
    fun updateRaiseHandState(
        userId: Long,
        isHandRaised: Boolean
    )

    /**
     * update all users hand down
     */
    fun updateAllHandsDown()

    /**
     * Update user moderator state
     *
     * @param userId id of a user
     * @param isModerator change state
     */
    fun updateUserModeratorState(
        userId: Long,
        isModerator: Boolean
    )

    /**
     * Check is participant is moderator
     *
     * @param participant for a user
     * @return boolean
     */
    fun isModerator(participant: KmeParticipant?): Boolean

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
        fun onParticipantMediaStatePayLoadChanged(payload: UserMediaStateChangedPayload)

        /**
         * Callback fired when medea state changed
         *
         * @param userId for detect who is muted
         * @param mediaStateType for change media state
         * @param stateValue for detect media device state
         */
        fun onParticipantMediaStateChanged(
            userId: Long,
            mediaStateType: KmeMediaStateType,
            stateValue: KmeMediaDeviceState
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
