package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeSettingsV2
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserSetting

/**
 * An interface for room settings
 */
interface IKmeSettingsModule : IKmeModule {

    /**
     * Subscribing for the room settings changes
     *
     * @param listener callback with [KmeSettingsListener] for indicating main events
     */
    fun subscribe(listener: KmeSettingsListener)

    /**
     * UpdateSettings for the room events related to change settings
     * for the users and for the room itself
     *
     * @param roomSetting
     * @param userSetting
     */
    fun updateSettings(roomSetting: KmeSettingsV2?, userSetting: KmeUserSetting)

    /**
     * Settings listener
     */
    interface KmeSettingsListener {

        /**
         * Callback fired always when settings updated
         *
         * @param roomSetting
         * @param userSetting
         */
        fun onSettingsChanged(roomSetting: KmeSettingsV2?, userSetting: KmeUserSetting)

        /**
         * Callback fired on moderator state changes. First time - on room state load
         */
        fun onModeratorStateChanged(isModerator: Boolean)
    }
}
