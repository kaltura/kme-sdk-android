package com.kme.kaltura.kmesdk

import com.kme.kaltura.kmesdk.di.controllersModule
import com.kme.kaltura.kmesdk.di.restModule
import com.kme.kaltura.kmesdk.rest.controller.IKmeSignInController
import org.koin.core.Koin
import org.koin.core.KoinComponent
import org.koin.dsl.koinApplication

class KME : KoinComponent {

    val signInController: IKmeSignInController by koin.inject()

    init {
        koin = koinApplication {
            modules(restModule)
            modules(controllersModule)
        }.koin
    }

    companion object {
        lateinit var koin: Koin
        private lateinit var instance: KME

        fun getInstance(): KME {
            if (!::instance.isInitialized) {
                instance = KME()
            }
            return instance
        }
    }

}
