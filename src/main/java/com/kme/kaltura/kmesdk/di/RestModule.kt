package com.kme.kaltura.kmesdk.di

import com.google.gson.GsonBuilder
import com.kme.kaltura.kmesdk.BuildConfig
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.rest.KmeCookieJar
import com.kme.kaltura.kmesdk.rest.KmeIntToBooleanTypeAdapter
import com.kme.kaltura.kmesdk.rest.KmeStringToBooleanTypeAdapter
import com.kme.kaltura.kmesdk.rest.KmeTokenInterceptor
import com.kme.kaltura.kmesdk.rest.service.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val restModule = module {
    single {
        GsonBuilder()
            .setLenient()
            .registerTypeAdapter(Boolean::class.javaObjectType, KmeIntToBooleanTypeAdapter())
            .registerTypeAdapter(Boolean::class.javaPrimitiveType, KmeIntToBooleanTypeAdapter())
            .registerTypeAdapter(Boolean::class.javaObjectType, KmeStringToBooleanTypeAdapter())
            .registerTypeAdapter(Boolean::class.javaPrimitiveType, KmeStringToBooleanTypeAdapter())
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

    single {
        KmeCookieJar(get())
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://${androidContext().getString(R.string.api_url)}/backend/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }

    single(
        named("Downloader")
    ) {
        Retrofit.Builder()
            .baseUrl("https://${androidContext().getString(R.string.api_url)}/backend/")
            .client(OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build())
            .build()
    }

    single { get<Retrofit>().create(KmeSignInApiService::class.java) }
    single { get<Retrofit>().create(KmeUserApiService::class.java) }
    single { get<Retrofit>().create(KmeRoomApiService::class.java) }
    single { get<Retrofit>().create(KmeRoomNotesApiService::class.java) }
    single { get<Retrofit>().create(KmeRoomRecordingApiService::class.java) }
    single { get<Retrofit>().create(KmeChatApiService::class.java) }
    single { get<Retrofit>().create(KmeMetadataApiService::class.java) }

    single { get<Retrofit>(named("Downloader")).create(KmeFileLoaderApiService::class.java) }
}
