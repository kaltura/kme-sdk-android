package com.kme.kaltura.kmesdk.di

import android.util.Log
import org.koin.core.Koin
import org.koin.core.KoinComponent
import org.koin.dsl.koinApplication

internal val sdkKoin = koinApplication {
    modules(restModule)
    modules(controllersModule)
}.koin

internal interface KmeKoinComponent : KoinComponent {

    override fun getKoin(): Koin {
        Log.e("TAG", "getKoin: $sdkKoin" )
        return sdkKoin
    }

}