package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.BuildConfig
import com.kme.kaltura.kmesdk.logger.IKmeLogger
import com.kme.kaltura.kmesdk.logger.KmeLoggerImpl
import org.koin.dsl.module

/**
 * Kme logger module
 */
val loggerModule = module {

    single<IKmeLogger> { KmeLoggerImpl(BuildConfig.DEBUG) }
}
