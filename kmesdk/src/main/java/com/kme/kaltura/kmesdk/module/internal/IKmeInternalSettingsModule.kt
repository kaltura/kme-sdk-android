package com.kme.kaltura.kmesdk.module.internal

import com.kme.kaltura.kmesdk.module.IKmeSettingsModule

/**
 * An interface for wrap actions for room settings
 * Only for internal SDK usage. This API not visible to the app level.
 */
interface IKmeInternalSettingsModule : IKmeSettingsModule {

    /**
     * Subscribing for the room events related to change settings
     * for the users and for the room itself
     */
    fun subscribe()

}
