package com.kme.kaltura.kmesdk.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.KoinComponent
import org.koin.dsl.koinApplication

object KmeKoinContext {

    private lateinit var context: Context

    internal val sdkKoin: Koin by lazy {
        koinApplication {
            androidContext(context)
            modules(restModule)
            modules(controllersModule)
            modules(preferencesModule)
            modules(webSocketModule)
            modules(webRTCModule)
        }.koin
    }

    fun init(appContext: Context) {
        context = appContext.applicationContext
    }

}

internal interface KmeKoinComponent : KoinComponent {

    override fun getKoin(): Koin {
        return KmeKoinContext.sdkKoin
    }

}
