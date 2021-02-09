package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.room.KmeChangeRoomSettingsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * An interface for room chat API calls
 */
interface KmeChatApiService {

    /**
     * Change visibility of public chat
     *
     * @param roomId id of a room
     * @param module KME module name
     * @param key KME chat key
     * @param value value flag
     * @return [KmeChangeRoomSettingsResponse] object in success case
     */
    @GET("room/setRoomSetting")
    suspend fun changePublicChatVisibility(
        @Query("room_id") roomId: Long,
        @Query("module") module: String,
        @Query("key") key: String,
        @Query("value") value: String
    ): KmeChangeRoomSettingsResponse

}
