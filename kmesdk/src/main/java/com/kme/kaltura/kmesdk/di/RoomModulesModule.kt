package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.controller.room.*
import com.kme.kaltura.kmesdk.controller.room.impl.*
import com.kme.kaltura.kmesdk.controller.room.internal.IKmeParticipantsInternalModule
import com.kme.kaltura.kmesdk.controller.room.internal.IKmePeerConnectionInternalModule
import com.kme.kaltura.kmesdk.controller.room.internal.IKmeRoomInternalModule
import com.kme.kaltura.kmesdk.controller.room.internal.IKmeSettingsInternalModule
import com.kme.kaltura.kmesdk.module.internal.IKmeInternalSettingsModule
import com.kme.kaltura.kmesdk.module.*
import com.kme.kaltura.kmesdk.module.impl.*
import com.kme.kaltura.kmesdk.module.internal.IKmeInternalDataModule
import com.kme.kaltura.kmesdk.module.internal.IKmeInternalParticipantModule
import com.kme.kaltura.kmesdk.module.internal.IKmeInternalPeerConnectionModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Kme room modules module
 */
val roomModules = module {

    single<IKmeInternalDataModule> { KmeInternalDataModuleImpl() }

    scope(named(KmeKoinScope.MODULES)) {
        scoped<IKmeRoomModule> {
            KmeRoomModuleImpl()
        } bind IKmeRoomInternalModule::class

        scoped<IKmePeerConnectionModule> {
            KmePeerConnectionModuleImpl()
        } bind IKmeInternalPeerConnectionModule::class

        scoped<IKmeParticipantModule> {
            KmeParticipantsModuleImpl()
        } bind IKmeInternalParticipantModule::class

        scoped<IKmeSettingsModule> {
            KmeSettingsModuleImpl()
        } bind IKmeInternalSettingsModule::class

        scoped<IKmeWebSocketModule> {
            KmeWebSocketModuleImpl(
                get(named("wsOkHttpClient")),
                get(named("main")),
                isMainSocket = true
            )
        }

        scoped<IKmeChatModule> { KmeChatModuleImpl() }
        scoped<IKmeNoteModule> { KmeNoteModuleImpl(androidContext()) }
        scoped<IKmeRecordingModule> { KmeRecordingModuleImpl() }
        scoped<IKmeAudioModule> { KmeAudioModuleImpl() }
        scoped<IKmeContentModule> { KmeContentModuleImpl() }
        scoped<IKmeTermsModule> { KmeTermsModuleImpl() }
        scoped<IKmeBreakoutModule> { KmeBreakoutModuleImpl() }
    }

    scope(named(KmeKoinScope.BOR_MODULES)) {
        scoped<IKmeWebSocketModule> {
            KmeWebSocketModuleImpl(
                get(named("wsOkHttpClient")),
                get(named("bor"))
            )
        }
    }

}
