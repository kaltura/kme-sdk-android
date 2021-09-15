package com.kme.kaltura.kmesdk.di

import android.util.Log
import com.kme.kaltura.kmesdk.controller.room.*
import com.kme.kaltura.kmesdk.controller.room.impl.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Kme room modules module
 */
val roomModules = module {

    scope(named(SCOPE_MODULES)) {
        scoped<IKmeRoomModule> {
            Log.e("TAG", "new KmeRoomModuleImpl")
            KmeRoomModuleImpl()
        }
        scoped<IKmeWebSocketModule> {
            Log.e("TAG", "new KmeWebSocketModuleImpl")
            KmeWebSocketModuleImpl()
        }
        scoped<IKmePeerConnectionModule> {
            Log.e("TAG", "new IKmePeerConnectionModule")
            KmePeerConnectionModuleImpl()
        }
        scoped<IKmeParticipantModule> {
            Log.e("TAG", "new IKmeParticipantModule")
            KmeParticipantModuleImpl()
        }
        scoped<IKmeChatModule> {
            Log.e("TAG", "new IKmeChatModule")
            KmeChatModuleImpl()
        }
        scoped<IKmeNoteModule> {
            Log.e("TAG", "new IKmeNoteModule")
            KmeNoteModuleImpl(androidContext())
        }
        scoped<IKmeRecordingModule> {
            Log.e("TAG", "new IKmeRecordingModule")
            KmeRecordingModuleImpl()
        }
        scoped<IKmeSettingsModule> {
            Log.e("TAG", "new IKmeSettingsModule")
            KmeSettingsModuleImpl()
        }
        scoped<IKmeAudioModule> {
            Log.e("TAG", "new IKmeAudioModule")
            KmeAudioModuleImpl()
        }
        scoped<IKmeContentModule> {
            Log.e("TAG", "new IKmeContentModule")
            KmeContentModuleImpl()
        }
    }

}
