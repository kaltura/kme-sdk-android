package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.controller.room.IKmePeerConnectionModule
import com.kme.kaltura.kmesdk.controller.room.impl.KmePeerConnectionModuleImpl
import com.kme.kaltura.kmesdk.webrtc.audio.IKmeAudioManager
import com.kme.kaltura.kmesdk.webrtc.audio.KmeAudioManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Kme WebRTC module
 */
val webRTCModule = module {

    factory<IKmePeerConnectionModule> { KmePeerConnectionModuleImpl(androidContext(), get()) }
    single<IKmeAudioManager> { KmeAudioManagerImpl(androidContext()) }

}
