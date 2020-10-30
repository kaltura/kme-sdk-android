package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.user.KmeGetUserInfoResponse
import retrofit2.http.GET

interface KmeUserApiService {

    @GET("user/getUserInformation")
    suspend fun getUserInfo(): KmeGetUserInfoResponse

}
