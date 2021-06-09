package com.kme.kaltura.kmesdk.controller.room

import androidx.lifecycle.LiveData

/**
 * An interface for room settings
 */
interface IKmeSettingsModule {

    val moderatorStateLiveData: LiveData<Boolean>

    /**
     * Subscribing for the room events related to change settings
     * for the users and for the room itself
     */
    fun subscribe()

}
