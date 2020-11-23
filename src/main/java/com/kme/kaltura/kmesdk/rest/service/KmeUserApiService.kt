package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.user.KmeGetUserInfoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface KmeUserApiService {

    @GET("user/getUserInformation")
    suspend fun getUserInfo(): KmeGetUserInfoResponse

    @GET("user/getUserInformation")
    suspend fun getUserInfo(
        @Query("room_alias") roomAlias: String
    ): KmeGetUserInfoResponse

}
