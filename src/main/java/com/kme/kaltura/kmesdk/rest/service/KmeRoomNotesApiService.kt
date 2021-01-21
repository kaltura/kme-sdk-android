package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.room.notes.*
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

    @FormUrlEncoded
    @POST("note/create")
    suspend fun createRoomNote(
        @Field("company_id") companyId: Long,
        @Field("room_id") roomId: Long
    ): KmeRoomNoteCreateResponse

    @FormUrlEncoded
    @POST("note/updateNoteName")
    suspend fun renameRoomNote(
        @Field("room_id") roomId: Long,
        @Field("note_id") noteId: String,
        @Field("newName") name: String
    ): KmeRoomNoteRenameResponse

    @FormUrlEncoded
    @POST("note/updateContent")
    suspend fun updateRoomNoteContent(
        @Field("room_id") roomId: Long,
        @Field("note_id") noteId: String,
        @Field("content") content: String,
        @Field("should_update_change_log") updateLogs: Boolean = false,
        @Field("html") html: String
    ): KmeRoomNoteUpdateContentResponse

    @FormUrlEncoded
    @POST("note/delete")
    suspend fun deleteRoomNote(
        @Field("room_id") roomId: Long,
        @Field("note_id") noteId: Long
    ): KmeDeleteRoomNoteResponse

}
