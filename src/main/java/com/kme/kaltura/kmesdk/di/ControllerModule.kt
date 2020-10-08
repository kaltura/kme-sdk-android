package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.controller.*
import com.kme.kaltura.kmesdk.controller.impl.KmeMetadataControllerImpl
import com.kme.kaltura.kmesdk.controller.impl.KmeRoomControllerImpl
import com.kme.kaltura.kmesdk.controller.impl.KmeSignInControllerImpl
import com.kme.kaltura.kmesdk.controller.impl.KmeUserControllerImpl
import org.koin.dsl.module

val controllersModule = module {

    single<IKmeSignInController> { KmeSignInControllerImpl() }
    single<IKmeUserController> { KmeUserControllerImpl() }
    single<IKmeRoomController> { KmeRoomControllerImpl() }
    single<IKmeMetadataController> { KmeMetadataControllerImpl() }

}
