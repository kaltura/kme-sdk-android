package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.metadata.GetMetadataResponse
import com.kme.kaltura.kmesdk.rest.response.metadata.GetTranslationsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface KmeMetadataApiService {

    @GET("fe/metadata")
    suspend fun getMetadata(): GetMetadataResponse

    @GET("fe/trans")
    suspend fun getTranslation(
        @Query("lang") lang: String
    ): GetTranslationsResponse

}
