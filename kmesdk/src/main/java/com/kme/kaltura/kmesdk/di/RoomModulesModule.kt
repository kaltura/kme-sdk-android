package com.kme.kaltura.kmesdk.di

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
        scoped<IKmeRoomModule> { KmeRoomModuleImpl() }
        scoped<IKmeWebSocketModule> {
            KmeWebSocketModuleImpl(
                get(named("wsOkHttpClient")),
                get(named("main"))
            )
        }
        scoped<IKmePeerConnectionModule> {
            KmePeerConnectionModuleImpl()
        } bind IKmeInternalPeerConnectionModule::class
        scoped<IKmeParticipantModule> {
            KmeParticipantModuleImpl()
        } bind IKmeInternalParticipantModule::class
        scoped<IKmeChatModule> { KmeChatModuleImpl() }
        scoped<IKmeNoteModule> { KmeNoteModuleImpl(androidContext()) }
        scoped<IKmeRecordingModule> { KmeRecordingModuleImpl() }
        scoped<IKmeSettingsModule> { KmeSettingsModuleImpl() }
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
