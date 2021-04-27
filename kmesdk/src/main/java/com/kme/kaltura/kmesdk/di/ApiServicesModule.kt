package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.rest.service.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val apiServicesModule = module {
    single { get<Retrofit>().create(KmeSignInApiService::class.java) }
    single { get<Retrofit>().create(KmeUserApiService::class.java) }
    single { get<Retrofit>().create(KmeRoomApiService::class.java) }
    single { get<Retrofit>().create(KmeRoomNotesApiService::class.java) }
    single { get<Retrofit>().create(KmeRoomRecordingApiService::class.java) }
    single { get<Retrofit>().create(KmeChatApiService::class.java) }
    single { get<Retrofit>().create(KmeMetadataApiService::class.java) }

    single { get<Retrofit>(named("Downloader")).create(KmeFileLoaderApiService::class.java) }
}