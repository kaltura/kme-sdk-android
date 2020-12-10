package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.room.KmeChangeRoomSettingsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface KmeChatApiService {

    @GET("room/setRoomSetting")
    suspend fun changePublicChatVisibility(
        @Query("room_id") roomId: Long,
        @Query("module") module: String,
        @Query("key") key: String,
        @Query("value") value: String
    ): KmeChangeRoomSettingsResponse

}
