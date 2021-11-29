package com.kme.kaltura.kmesdk.controller.room

import androidx.lifecycle.LiveData
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeSettingsV2

/**
 * An interface for room settings
 */
interface IKmeSettingsModule : IKmeModule {

    val moderatorStateLiveData: LiveData<Boolean>
    val settingsChangedLiveData: LiveData<Boolean>
    val userSettingsChangedLiveData: LiveData<KmeSettingsV2>

    /**
     * Subscribing for the room events related to change settings
     * for the users and for the room itself
     */
    fun subscribe()


    /**
     * UpdateSettings for the room events related to change settings
     * for the users and for the room itself
     *
     * @param settings
     */
    fun updateSettings(settings: KmeSettingsV2?)
}
