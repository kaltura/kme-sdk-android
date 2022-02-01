package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.controller.room.*
import com.kme.kaltura.kmesdk.controller.room.impl.*
import com.kme.kaltura.kmesdk.controller.room.internal.IKmeParticipantsInternalModule
import com.kme.kaltura.kmesdk.controller.room.internal.IKmePeerConnectionInternalModule
import com.kme.kaltura.kmesdk.controller.room.internal.IKmeSettingsInternalModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Kme room modules module
 */
val roomModules = module {

    scope(named(KmeKoinScope.MODULES)) {
        scoped<IKmeRoomModule> { KmeRoomModuleImpl() }
        scoped<IKmeWebSocketModule> { KmeWebSocketModuleImpl() }
        scoped<IKmePeerConnectionModule> {
            KmePeerConnectionModuleImpl()
        } bind IKmePeerConnectionInternalModule::class
        scoped<IKmeParticipantModule> {
            KmeParticipantsModuleImpl()
        } bind IKmeParticipantsInternalModule::class
        scoped<IKmeChatModule> { KmeChatModuleImpl() }
        scoped<IKmeNoteModule> { KmeNoteModuleImpl(androidContext()) }
        scoped<IKmeRecordingModule> { KmeRecordingModuleImpl() }
        scoped<IKmeSettingsModule> {
            KmeSettingsModuleImpl()
        } bind IKmeSettingsInternalModule::class
        scoped<IKmeAudioModule> { KmeAudioModuleImpl() }
        scoped<IKmeContentModule> { KmeContentModuleImpl() }
        scoped<IKmeTermsModule> { KmeTermsModuleImpl() }
    }

}
