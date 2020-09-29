package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface KmeUserApiService {

    @FormUrlEncoded
    @POST("user/getUserInformation")
    suspend fun getUser(): KmeResponse

}
