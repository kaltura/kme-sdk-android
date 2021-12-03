package com.kme.kaltura.kmesdk.controller.room

import androidx.lifecycle.LiveData

/**
 * An interface for room settings
 */
interface IKmeSettingsModule : IKmeModule {

    val moderatorStateLiveData: LiveData<Boolean>
    val settingsChangedLiveData: LiveData<Boolean>

}
