package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.controller.room.*
import com.kme.kaltura.kmesdk.controller.room.impl.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Kme room modules module
 */
val roomModules = module {

    single<IKmeRoomModule> { KmeRoomModuleImpl() }
    single<IKmeWebSocketModule> { KmeWebSocketModuleImpl() }
    single<IKmeChatModule> { KmeChatModuleImpl() }
    single<IKmeNoteModule> { KmeNoteModuleImpl(androidContext()) }
    single<IKmeRecordingModule> { KmeRecordingModuleImpl() }
    single<IKmeSettingsModule> { KmeSettingsModuleImpl() }
    single<IKmeDesktopShareModule> { KmeDesktopShareModuleImpl() }
    single<IKmeAudioModule> { KmeAudioModuleImpl() }

}
