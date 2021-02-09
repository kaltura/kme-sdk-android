package com.kme.kaltura.kmesdk.controller.room

/**
 * An interface for room settings
 */
interface IKmeSettingsModule {

    /**
     * Subscribing for the room events related to change settings
     * for the users and for the room itself
     */
    fun subscribe()

}
