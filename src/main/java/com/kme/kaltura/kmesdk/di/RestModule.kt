package com.kme.kaltura.kmesdk.di

import com.google.gson.GsonBuilder
import com.kme.kaltura.kmesdk.BuildConfig
import com.kme.kaltura.kmesdk.rest.KmeBooleanTypeAdapter
import com.kme.kaltura.kmesdk.rest.service.KmeRoomApiService
import com.kme.kaltura.kmesdk.rest.service.KmeSignInApiService
import com.kme.kaltura.kmesdk.rest.service.KmeUserApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val restModule = module {

    single {
        GsonBuilder()
            .setLenient()
            .registerTypeAdapter(Boolean::class.javaObjectType, KmeBooleanTypeAdapter())
            .registerTypeAdapter(Boolean::class.javaPrimitiveType, KmeBooleanTypeAdapter())
            .create()
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
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://smart.newrow.com/backend/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }

    single { get<Retrofit>().create(KmeSignInApiService::class.java) }
    single { get<Retrofit>().create(KmeUserApiService::class.java) }
    single { get<Retrofit>().create(KmeRoomApiService::class.java) }

}
