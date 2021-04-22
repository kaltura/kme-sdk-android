package com.kme.kaltura.kmesdk.di

import com.google.gson.GsonBuilder
import com.kme.kaltura.kmesdk.BuildConfig
import com.kme.kaltura.kmesdk.rest.DynamicRetrofit
import com.kme.kaltura.kmesdk.rest.KmeCookieJar
import com.kme.kaltura.kmesdk.rest.KmeTokenInterceptor
import com.kme.kaltura.kmesdk.rest.adapter.*
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData
import com.kme.kaltura.kmesdk.rest.response.room.KmeIntegrations
import com.kme.kaltura.kmesdk.rest.service.*
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

/**
 * Kme REST module
 */
val restModule = module {
    single {
        GsonBuilder()
            .setLenient()
            .registerTypeAdapter(Boolean::class.javaObjectType, KmeIntToBooleanTypeAdapter())
            .registerTypeAdapter(Boolean::class.javaPrimitiveType, KmeIntToBooleanTypeAdapter())
            .registerTypeAdapter(Boolean::class.javaObjectType, KmeStringToBooleanTypeAdapter())
            .registerTypeAdapter(Boolean::class.javaPrimitiveType, KmeStringToBooleanTypeAdapter())
            .registerTypeAdapter(
                KmeResponseData::class.javaObjectType,
                KmeResponseDataTypeAdapter()
            )
            .registerTypeAdapter(KmeWhiteboardPath::class.java, KmeWhiteboardPathTypeAdapter())
            .registerTypeAdapter(KmeIntegrations::class.java, KmeIntegrationsAdapter())
            .create()
    }
    single {
        KmeTokenInterceptor(get())
    }
    single {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
    }
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .cookieJar(get<KmeCookieJar>())
            .addInterceptor(get<KmeTokenInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single(named("Downloader")) {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        KmeCookieJar(get())
    }

    single {
        DynamicRetrofit(androidContext(), get())
    }

    single(
        named("Downloader")
    ) {
        DynamicRetrofit(androidContext(), get(named("Downloader")))
    }

    single { get<DynamicRetrofit>().create(KmeSignInApiService::class.java) }
    single { get<DynamicRetrofit>().create(KmeUserApiService::class.java) }
    single { get<DynamicRetrofit>().create(KmeRoomApiService::class.java) }
    single { get<DynamicRetrofit>().create(KmeRoomNotesApiService::class.java) }
    single { get<DynamicRetrofit>().create(KmeRoomRecordingApiService::class.java) }
    single { get<DynamicRetrofit>().create(KmeChatApiService::class.java) }
    single { get<DynamicRetrofit>().create(KmeMetadataApiService::class.java) }

    single { get<DynamicRetrofit>(named("Downloader")).create(KmeFileLoaderApiService::class.java) }
}
