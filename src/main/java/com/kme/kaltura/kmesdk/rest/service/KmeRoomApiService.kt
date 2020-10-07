package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomInfoResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface KmeRoomApiService {

    @GET("room/getRoomListForCompany")
    suspend fun getRooms(
        @Query("company_id") companyId: Long,
        @Query("page_number") pages: Long,
        @Query("limit") limit: Long
    ): KmeGetRoomsResponse

    @GET("room/roomInfoByAlias")
    suspend fun getRoomInfo(
        @Query("alias") alias: String,
        @Query("with_viewed_files") withFiles: Int,
        @Query("check_permission") checkPermission: Int
    ): KmeGetRoomInfoResponse

}
