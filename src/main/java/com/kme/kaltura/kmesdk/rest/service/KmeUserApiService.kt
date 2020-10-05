package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.user.KmeGetUserInfoResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface KmeUserApiService {

    @POST("user/getUserInformation")
    suspend fun getUserInfo(
        @Query("access-token") accessToken: String
    ): KmeGetUserInfoResponse

}
