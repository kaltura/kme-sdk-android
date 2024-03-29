package com.kme.kaltura.kmesdk.di

import com.google.gson.JsonParser
import com.kme.kaltura.kmesdk.ws.KmeMessageManager
import com.kme.kaltura.kmesdk.ws.KmeMessageParser
import com.kme.kaltura.kmesdk.ws.KmeWebSocketHandler
import com.kme.kaltura.kmesdk.ws.KmeWebSocketType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

/**
 * Kme web socket module
 */
val webSocketModule = module {

    single(named("wsOkHttpClient")) {
        OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .addInterceptor(get<HttpLoggingInterceptor>())
            .retryOnConnectionFailure(true)
            .build()
    }

    single { JsonParser() }
    single { KmeMessageParser(get(), get()) }
    single { KmeMessageManager(get()) }

    single(named(KmeWebSocketType.MAIN)) {
        KmeWebSocketHandler(
            get(),
            get(),
            KmeWebSocketType.MAIN,
            get()
        )
    }
    single(named(KmeWebSocketType.BREAKOUT)) {
        KmeWebSocketHandler(
            get(),
            get(),
            KmeWebSocketType.BREAKOUT,
            get()
        )
    }

}
