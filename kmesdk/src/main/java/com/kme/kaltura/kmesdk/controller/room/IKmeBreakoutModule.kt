package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage.RoomModuleSettingsChangedPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeBreakoutRoomMessageType

/**
 * An interface for actions with breakout rooms
 */
interface IKmeBreakoutModule : IKmeModule {

    /**
     * Subscribing for the room events related breakout rooms
     */
    fun subscribe()

    /**
     * Handle breakout room setting changes from main room
     */
    fun onSettingChanged(payload: RoomModuleSettingsChangedPayload)

    /**
     * Setting events listener
     */
    fun setEventsListener(listener: IKmeBreakoutEvents)

    /**
     * Assign self to specific breakout room
     */
    fun assignSelfToBor(breakoutRoomId: Long)

    /**
     * Assign user to specific breakout room
     */
    fun assignUserToBor(
        userId: Long,
        breakoutRoomId: Long
    )

    /**
     * Call instructor
     */
    fun callToInstructor()

    /**
     * Getting list of breakout rooms
     */
    fun getBreakoutState(): KmeBreakoutModuleMessage.BreakoutRoomState?

    /**
     * Checking is breakout rooms currently active
     */
    fun isActive(): Boolean

    /**
     * Getting breakout room in case that user assigned to any bor
     */
    fun getAssignedBreakoutRoom(): KmeBreakoutModuleMessage.BreakoutRoom?

    /**
     * Subscribing for the room events related breakout rooms
     */
    interface IKmeBreakoutEvents {

        /**
         * Event triggers when current participant going to be moved to another room
         */
        fun onBreakoutRoomStart(
            roomId: Long,
            roomAlias: String,
            selfAssigned: Boolean
        )

        /**
         * Event triggers when administrator closes the breakout rooms
         */
        fun onBreakoutRoomStop()

        /**
         * Event triggers when participant calls instructor
         */
        fun onBreakoutCallInstructor(
            roomId: Long,
            userId: Long?
        )

        /**
         * Event triggers when instructor send an announcement
         */
        fun onBreakoutInstructorMessage(
            messageType: KmeBreakoutRoomMessageType,
            messageMetadata: KmeBreakoutModuleMessage.BreakoutMessageMetadata,
        )

        /**
         * Event triggers when instructor send an increases a time for breakout room
         */
        fun onBreakoutTimeExtended()

        /**
         * Event triggers when instructor send an increases a time for breakout room
         */
        fun onBreakoutRoomStateChanged()

    }

}
