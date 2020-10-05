package com.kme.kaltura.kmesdk

import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.rest.controller.IKmeRoomController
import com.kme.kaltura.kmesdk.rest.controller.IKmeSignInController
import com.kme.kaltura.kmesdk.rest.controller.IKmeUserController
import org.koin.core.inject

class KME : KmeKoinComponent {

    val signInController: IKmeSignInController by inject()
    val userController: IKmeUserController by inject()
    val roomController: IKmeRoomController by inject()

    companion object {
        private lateinit var instance: KME

        fun getInstance(): KME {
            if (!::instance.isInitialized) {
                instance = KME()
            }
            return instance
        }
    }

}
