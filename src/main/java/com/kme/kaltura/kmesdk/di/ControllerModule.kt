package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.rest.controller.IKmeSignInController
import com.kme.kaltura.kmesdk.rest.controller.KmeSignInControllerImpl
import org.koin.dsl.module

val controllersModule = module {

    single<IKmeSignInController> { KmeSignInControllerImpl() }

}
