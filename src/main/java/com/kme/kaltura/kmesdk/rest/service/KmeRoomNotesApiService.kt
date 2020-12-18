package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeGetRoomNotesResponse
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeRoomNoteDownloadUrlResponse
import retrofit2.http.*

interface KmeRoomNotesApiService {

    @GET("note/getRoomNotes")
    suspend fun getRoomNotes(
        @Query("company_id") companyId: Long,
        @Query("room_id") roomId: Long
    ): KmeGetRoomNotesResponse

    @FormUrlEncoded
    @POST("note/downloadNote")
    suspend fun getDownloadRoomNoteUrl(
        @Field("room_id") roomId: Long,
        @Field("note_id") noteId: Long,
        @Field("saveToFiles") saveToFiles: Boolean
    ): KmeRoomNoteDownloadUrlResponse

}
