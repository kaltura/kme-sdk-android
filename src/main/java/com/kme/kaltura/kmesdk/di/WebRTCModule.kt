package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.controller.IKmeWebRTCController
import com.kme.kaltura.kmesdk.controller.impl.KmeWebRTCControllerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val webRTCModule = module {

    single<IKmeWebRTCController> { KmeWebRTCControllerImpl(androidContext(), get()) }

}
