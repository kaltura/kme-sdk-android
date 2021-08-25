package com.kme.kaltura.kmesdk.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.KoinComponent
import org.koin.dsl.koinApplication

/**
 * Base class for the KME controllers which has an access for Koin container
 */
object KmeKoinContext {

    private lateinit var context: Context

    internal val sdkKoin: Koin by lazy {
        if (!::context.isInitialized) {
            throw IllegalStateException("SDK is not initialized. Try to use KME.init() first.")
        }
        koinApplication {
            androidContext(context)
            modules(restModule)
            modules(apiServicesModule)
            modules(controllersModule)
            modules(roomModules)
            modules(contentShareViewModels)
            modules(preferencesModule)
            modules(webSocketModule)
            modules(webRTCModule)
            modules(helpersModule)
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
