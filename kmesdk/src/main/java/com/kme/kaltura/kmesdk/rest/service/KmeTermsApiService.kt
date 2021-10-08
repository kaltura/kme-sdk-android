package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.terms.KmeGetTermsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * An interface for terms data API calls
 */
interface KmeTermsApiService {

    /**
     * Getting terms message for specific company
     *
     * @param companyId id of a company
     * @return [company_id] object in success case
     */
    @GET("company/terms")
    suspend fun getTerms(
        @Query("room_id") roomId: Long,
        @Query("company_id") companyId: Long,
    ): KmeGetTermsResponse

}
