package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.controller.IKmeMetadataController
import com.kme.kaltura.kmesdk.controller.IKmeSignInController
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeMetadataControllerImpl
import com.kme.kaltura.kmesdk.controller.impl.KmeSignInControllerImpl
import com.kme.kaltura.kmesdk.controller.impl.KmeUserControllerImpl
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.room.impl.KmeRoomControllerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Kme controllers module
 */
val controllersModule = module {

    single<IKmeMetadataController> { KmeMetadataControllerImpl() }
    single<IKmeSignInController> { KmeSignInControllerImpl(androidContext()) }
    single<IKmeUserController> { KmeUserControllerImpl() }

    scope(named(SCOPE_CONTROLLER)) {
        scoped<IKmeRoomController> {
            KmeRoomControllerImpl(androidContext())
        }
    }

}
