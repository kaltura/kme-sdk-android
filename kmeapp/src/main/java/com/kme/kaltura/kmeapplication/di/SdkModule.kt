package com.kme.kaltura.kmeapplication.di

import com.kme.kaltura.kmesdk.KME
import org.koin.dsl.module

val sdk = module {
    single { KME.getInstance() }
}
