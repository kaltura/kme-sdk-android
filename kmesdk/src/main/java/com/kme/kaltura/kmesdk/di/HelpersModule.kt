package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.content.playkit.KmeDefaultPlayerEventHandler
import com.kme.kaltura.kmesdk.content.poll.KmeDefaultPollEventHandler
import org.koin.dsl.module

/**
 * Kme helpers module
 */
val helpersModule = module {

    single { KmeDefaultPlayerEventHandler(get()) }
    single { KmeDefaultPollEventHandler(get()) }

}
