package com.kme.kaltura.kmesdk.controller.impl

import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.isSDKInitialized

abstract class KmeController : KmeKoinComponent {

    init {
        if (!isSDKInitialized) {
            throw Exception("SDK is not initialized!")
        }
    }

}
