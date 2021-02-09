package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.user.KmeGetUserInfoResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * An interface for actual user information details API calls
 */
interface KmeUserApiService {

    /**
     * Getting actual user information
     *
     * @return [KmeGetUserInfoResponse] object in success case
     */
    @GET("user/getUserInformation")
    suspend fun getUserInfo(): KmeGetUserInfoResponse

    /**
     * Getting actual user information by specific room
     *
     * @param roomAlias alias of a room
     * @return [KmeGetUserInfoResponse] object in success case
     */
    @GET("user/getUserInformation")
    suspend fun getUserInfo(
        @Query("room_alias") roomAlias: String
    ): KmeGetUserInfoResponse

}
