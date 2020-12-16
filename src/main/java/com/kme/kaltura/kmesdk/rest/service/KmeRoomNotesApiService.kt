package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeGetRoomNotesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface KmeRoomNotesApiService {

    @GET("note/getRoomNotes")
    suspend fun getRoomNotes(
        @Query("company_id") companyId: Long,
        @Query("room_id") roomId: Long
    ): KmeGetRoomNotesResponse

}
