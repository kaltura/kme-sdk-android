package com.kme.kaltura.kmesdk.controller.room

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
     * Subscribing for the room events related breakout rooms
     */
    interface IKmeBreakoutEvents {

        /**
         * Events triggers when current participant going to be moved to another room
         */
        fun onBreakoutRoomStart(
            roomId: Long,
            roomAlias: String
        )

        /**
         * Events triggers when administrator closes the breakout rooms
         */
        fun onBreakoutRoomStop()

    }

}
