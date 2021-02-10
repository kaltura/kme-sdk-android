package com.kme.kaltura.kmeapplication.di

import com.kme.kaltura.kmeapplication.prefs.AppPreferencesImpl
import com.kme.kaltura.kmeapplication.prefs.IAppPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val preferencesModule = module {

    single<IAppPreferences> { AppPreferencesImpl(androidContext()) }

}
