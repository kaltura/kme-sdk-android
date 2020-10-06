package com.kme.kaltura.kmesdk.rest.controller

import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.isSDKInitialized

abstract class KmeController : KmeKoinComponent {

    init {
        if (!isSDKInitialized) {
            throw Exception("SDK is not initialized!")
        }
    }

}
