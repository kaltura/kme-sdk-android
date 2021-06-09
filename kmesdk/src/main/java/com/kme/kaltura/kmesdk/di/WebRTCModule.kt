package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnection
import com.kme.kaltura.kmesdk.webrtc.peerconnection.impl.KmePeerConnectionImpl
import com.kme.kaltura.kmesdk.webrtc.audio.IKmeAudioManager
import com.kme.kaltura.kmesdk.webrtc.audio.KmeAudioManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Kme WebRTC module
 */
val webRTCModule = module {

    factory<IKmePeerConnection> { KmePeerConnectionImpl(androidContext(), get()) }
    single<IKmeAudioManager> { KmeAudioManagerImpl(androidContext()) }

}
