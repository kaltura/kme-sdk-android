package com.kme.kaltura.kmesdk.di

import com.google.gson.GsonBuilder
import com.kme.kaltura.kmesdk.BuildConfig
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.rest.CsrfTokenInterceptor
import com.kme.kaltura.kmesdk.rest.KmeChangeableBaseUrlInterceptor
import com.kme.kaltura.kmesdk.rest.KmeCookieJar
import com.kme.kaltura.kmesdk.rest.KmeTokenInterceptor
import com.kme.kaltura.kmesdk.rest.adapter.*
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData
import com.kme.kaltura.kmesdk.rest.response.room.KmeIntegrations
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
            .registerTypeAdapter(BreakoutRoomStatusPayload::class.java, KmeBreakoutRoomTypeAdapter())
            .registerTypeAdapter(BreakoutAddRoomPayload::class.java, KmeBreakoutAddRoomTypeAdapter())
            .registerTypeAdapter(BreakoutChangeNamePayload::class.java, KmeBreakoutChangeRoomNameTypeAdapter())
            .create()
    }

    single {
        KmeTokenInterceptor(get())
    }

    single {
        CsrfTokenInterceptor(get())
    }

    single {
        KmeChangeableBaseUrlInterceptor(androidContext(), get())
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
            .addInterceptor(get<KmeChangeableBaseUrlInterceptor>())
            .addInterceptor(get<KmeTokenInterceptor>())
            .addInterceptor(get<CsrfTokenInterceptor>())
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
        val baseUrl = if (BuildConfig.DEBUG) {
            "https://${androidContext().getString(R.string.staging_api_url)}/backend/"
        } else {
            "https://${androidContext().getString(R.string.production_api_url)}/backend/"
        }
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }

    single(
        named("Downloader")
    ) {
        val baseUrl = if (BuildConfig.DEBUG) {
            "https://${androidContext().getString(R.string.staging_api_url)}/backend/"
        } else {
            "https://${androidContext().getString(R.string.production_api_url)}/backend/"
        }
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(get(named("Downloader")))
            .build()
    }

}
