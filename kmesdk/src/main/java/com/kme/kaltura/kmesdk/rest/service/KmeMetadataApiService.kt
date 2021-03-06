package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.metadata.GetMetadataResponse
import com.kme.kaltura.kmesdk.rest.response.metadata.GetTranslationsResponse
import com.kme.kaltura.kmesdk.rest.response.metadata.KeepAliveResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * An interface for getting metadata API calls
 */
interface KmeMetadataApiService {

    /**
     * Getting metadata for global usage
     *
     * @return [GetMetadataResponse] object in success case
     */
    @GET("fe/metadata")
    suspend fun getMetadata(): GetMetadataResponse

    /**
     * Keep user alive. Update CSRF token
     *
     * @return [GetMetadataResponse] object in success case
     */
    @GET("user/keepAlive")
    suspend fun keepAlive(): KeepAliveResponse

    /**
     * Getting translations strings for specific language
     *
     * @param lang language for translations
     * @return [GetTranslationsResponse] object in success case
     */
    @GET("fe/trans")
    suspend fun getTranslation(
        @Query("lang") lang: String
    ): GetTranslationsResponse

    /**
     * Getting debug information about session
     */
    @GET("site/sesInfo")
    suspend fun sessionInfo(): String

}
