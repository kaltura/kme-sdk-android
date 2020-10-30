package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePreferencesImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val preferencesModule = module {

    single<IKmePreferences> { KmePreferencesImpl(androidContext()) }

}
