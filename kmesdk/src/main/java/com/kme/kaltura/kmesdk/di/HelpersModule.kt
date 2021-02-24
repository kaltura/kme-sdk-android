package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.content.playkit.KmeDefaultPlayerEventHandler
import org.koin.dsl.module

/**
 * Kme helpers module
 */
val helpersModule = module {

    single { KmeDefaultPlayerEventHandler(get()) }

}
