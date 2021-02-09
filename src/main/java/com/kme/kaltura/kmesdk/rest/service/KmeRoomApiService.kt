package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomInfoResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomsResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetWebRTCServerResponse
import com.kme.kaltura.kmesdk.ws.message.type.KmePlatformType
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * An interface for room data API calls
 */
interface KmeRoomApiService {

    /**
     * Getting all rooms for specific company
     *
     * @param companyId id of a company
     * @param pages page number
     * @param limit count of rooms per page
     * @return [KmeGetRoomsResponse] object in success case
     */
    @GET("room/getRoomListForCompany")
    suspend fun getRooms(
        @Query("company_id") companyId: Long,
        @Query("page_number") pages: Long,
        @Query("limit") limit: Long
    ): KmeGetRoomsResponse

    /**
     * Getting room info by alias
     *
     * @param alias alias of a room
     * @param withFiles
     * @param checkPermission
     * @return [KmeGetRoomInfoResponse] object in success case
     */
    @GET("room/roomInfoByAlias")
    suspend fun getRoomInfo(
        @Query("alias") alias: String,
        @Query("with_viewed_files") withFiles: Int,
        @Query("check_permission") checkPermission: Int
    ): KmeGetRoomInfoResponse

    /**
     * Getting data for p2p connection
     *
     * @param roomAlias alias of a room
     * @param deviceType device type flag
     * @return [KmeGetWebRTCServerResponse] object in success case
     */
    @GET("room/getWebrtcLiveServer")
    suspend fun getWebRTCLiveServer(
        @Query("room_alias") roomAlias: String,
        @Query("device_type") deviceType: KmePlatformType = KmePlatformType.MOBILE
    ): KmeGetWebRTCServerResponse

}
