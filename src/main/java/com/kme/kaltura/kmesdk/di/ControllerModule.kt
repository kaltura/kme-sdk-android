package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.controller.IKmeMetadataController
import com.kme.kaltura.kmesdk.controller.IKmeSignInController
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeMetadataControllerImpl
import com.kme.kaltura.kmesdk.controller.impl.KmeSignInControllerImpl
import com.kme.kaltura.kmesdk.controller.impl.KmeUserControllerImpl
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
    single<IKmeRoomController> { KmeRoomControllerImpl(androidContext()) }

    single<IKmeWebSocketModule> { KmeWebSocketControllerImpl() }
    single<IKmeChatModule> { KmeChatModuleImpl() }
    single<IKmeNoteModule> { KmeNoteModuleImpl(androidContext()) }
    single<IKmeRecordingModule> { KmeRecordingModuleImpl() }
    single<IKmeSettingsModule> { KmeSettingsModuleImpl() }
    single<IKmeDesktopShareModule> { KmeDesktopShareModuleImpl() }
    single<IKmeAudioModule> { KmeAudioModuleImpl() }

}
