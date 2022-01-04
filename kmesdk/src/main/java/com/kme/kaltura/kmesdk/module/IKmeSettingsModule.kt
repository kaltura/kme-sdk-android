package com.kme.kaltura.kmesdk.module

import androidx.lifecycle.LiveData

/**
 * An interface for room settings
 */
interface IKmeSettingsModule : IKmeModule {

    val moderatorStateLiveData: LiveData<Boolean>
    val settingsChangedLiveData: LiveData<Boolean>

    /**
     * Subscribing for the room events related to change settings
     * for the users and for the room itself
     */
    fun subscribe()

}
