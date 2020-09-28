package com.kme.kaltura.kmesdk

import com.kme.kaltura.kmesdk.rest.services.KmeApiService
import com.kme.kaltura.kmesdk.rest.services.KmeAuthService
import com.kme.kaltura.kmesdk.rest.services.KmeRoomService
import org.koin.core.KoinComponent

class KME : KoinComponent {

//    val rest: KmeRestClient by koin.inject()

    companion object {
        private lateinit var instance: KME

        fun getInstance(): KME {
            if (!::instance.isInitialized) {
                instance = KME()
            }
            return instance
        }
    }

    fun getAuthService(): KmeApiService {
        return KmeAuthService()
    }

    fun getRoomService(): KmeApiService {
        return KmeRoomService()
    }

}
