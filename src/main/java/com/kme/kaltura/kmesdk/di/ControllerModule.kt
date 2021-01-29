package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.controller.*
import com.kme.kaltura.kmesdk.controller.impl.*
import com.kme.kaltura.kmesdk.controller.room.impl.KmeSettingsModuleImpl
import com.kme.kaltura.kmesdk.controller.room.impl.KmeWebSocketControllerImpl
import com.kme.kaltura.kmesdk.controller.room.*
import com.kme.kaltura.kmesdk.controller.room.impl.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Kme controllers module
 */
val controllersModule = module {

    single<IKmeMetadataController> { KmeMetadataControllerImpl() }
    single<IKmeSignInController> { KmeSignInControllerImpl() }
    single<IKmeUserController> { KmeUserControllerImpl() }
    single<IKmeWebSocketModule> { KmeWebSocketControllerImpl() }
    single<IKmeRoomController> { KmeRoomControllerImpl(androidContext()) }
    single<IKmeNoteModule> { KmeNoteModuleImpl(androidContext()) }
    single<IKmeRecordingModule> { KmeRecordingModuleImpl() }
    single<IKmeSettingsModule> { KmeSettingsModuleImpl() }
    single<IKmeChatModule> { KmeChatModuleImpl() }
    single<IKmeAudioModule> { KmeAudioModuleImpl() }

}
