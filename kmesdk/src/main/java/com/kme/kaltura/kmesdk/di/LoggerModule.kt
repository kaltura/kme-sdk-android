package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.logger.IKmeLogger
import com.kme.kaltura.kmesdk.logger.KmeLoggerImpl
import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePreferencesImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Kme logger module
 */
val loggerModule = module {

    single<IKmeLogger> { KmeLoggerImpl() }
}
