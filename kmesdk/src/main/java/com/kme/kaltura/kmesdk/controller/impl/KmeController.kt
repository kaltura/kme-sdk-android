package com.kme.kaltura.kmesdk.controller.impl

import android.util.Log
import com.kme.kaltura.kmesdk.di.KmeKoinComponent

/**
 * Base abstract class for the KME controllers
 */
abstract class KmeController : KmeKoinComponent {

    init {
        Log.e("KmeController", "${this.javaClass.simpleName} ${hashCode()} created")
    }

}
