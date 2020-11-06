package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.controller.IKmePeerConnectionController
import com.kme.kaltura.kmesdk.controller.impl.KmePeerConnectionControllerImpl
import com.kme.kaltura.kmesdk.webrtc.audio.IKmeAudioManager
import com.kme.kaltura.kmesdk.webrtc.audio.KmeAudioManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val webRTCModule = module {

    factory<IKmePeerConnectionController> { KmePeerConnectionControllerImpl(androidContext(), get()) }
    single<IKmeAudioManager> { KmeAudioManagerImpl(androidContext()) }

}
