package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomsResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface KmeRoomApiService {

    @POST("room/getRoomListForCompany")
    suspend fun getRooms(
        @Query("access-token") accessToken: String,
        @Query("company_id") companyId: Long,
        @Query("page_number") pages: Long,
        @Query("limit") limit: Long
    ): KmeGetRoomsResponse

}
