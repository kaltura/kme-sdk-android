package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.content.playkit.KmeDefaultPlayerEventHandler
import com.kme.kaltura.kmesdk.controller.room.IKmePeerConnection
import com.kme.kaltura.kmesdk.controller.room.impl.KmePeerConnectionImpl
import com.kme.kaltura.kmesdk.webrtc.audio.IKmeAudioManager
import com.kme.kaltura.kmesdk.webrtc.audio.KmeAudioManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Kme helpers module
 */
val helpersModule = module {

    single { KmeDefaultPlayerEventHandler(get()) }

}
