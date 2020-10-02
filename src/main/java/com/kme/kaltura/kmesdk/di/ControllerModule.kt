package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.rest.controller.*
import org.koin.dsl.module

val controllersModule = module {

    single<IKmeSignInController> { KmeSignInControllerImpl() }
    single<IKmeUserController> { KmeUserControllerImpl() }
    single<IKmeRoomController> { KmeRoomControllerImpl() }

}
