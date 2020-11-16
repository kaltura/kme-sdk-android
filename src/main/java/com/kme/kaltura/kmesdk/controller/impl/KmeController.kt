package com.kme.kaltura.kmesdk.controller.impl

import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.di.KmeKoinComponent

abstract class KmeController : KmeKoinComponent {

    init {
        if (!KME.getInstance().isSDKInitialized) {
            throw Exception("SDK is not initialized!")
        }
    }

}
