package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage.RoomModuleSettingsChangedPayload

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
     * Subscribing for the room events related breakout rooms
     */
    interface IKmeBreakoutEvents {

        /**
         * Event triggers when current participant going to be moved to another room
         */
        fun onBreakoutRoomStart(
            roomId: Long,
            roomAlias: String
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
            userId: Long
        )

        /**
         * Event triggers when instructor send an announcement
         */
        fun onBreakoutInstructorMessage(
            userId: Long,
            text: String,
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
