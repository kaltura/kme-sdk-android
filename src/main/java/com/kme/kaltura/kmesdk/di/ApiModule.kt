package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.rest.KmeRestClient
import org.koin.dsl.module

val restModule = module {
    single { KmeRestClient() }
}