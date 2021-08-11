package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.room.notes.*
import retrofit2.http.*

/**
 * An interface for notes API calls
 */
interface KmeRoomNotesApiService {

    /**
     * Getting all notes for specific room
     *
     * @param companyId id of a company
     * @param roomId id of a room
     * @return [KmeGetRoomNotesResponse] object in success case
     */
    @GET("note/getRoomNotes")
    suspend fun getRoomNotes(
        @Query("company_id") companyId: Long,
        @Query("room_id") roomId: Long
    ): KmeGetRoomNotesResponse

    /**
     * Getting specific note
     *
     * @param roomId id of a room
     * @param noteId id of a note
     * @return [KmeGetRoomNotesResponse] object in success case
     */
    @GET("note/getNote")
    suspend fun getRoomNote(
        @Query("room_id") roomId: Long,
        @Query("note_id") noteId: Long,
    ): KmeGetRoomNoteResponse

    /**
     * Getting an url for download note as pdf file
     *
     * @param roomId id of a room
     * @param noteId id of a note
     * @param saveToFiles save to room files folder
     * @return [KmeRoomNoteDownloadUrlResponse] object in success case
     */
    @FormUrlEncoded
    @POST("note/downloadNote")
    suspend fun getDownloadRoomNoteUrl(
        @Field("room_id") roomId: Long,
        @Field("note_id") noteId: Long,
        @Field("saveToFiles") saveToFiles: Boolean
    ): KmeRoomNoteDownloadUrlResponse

    /**
     * Creates a new note in the room
     *
     * @param companyId id of a company
     * @param roomId id of a room
     * @return [KmeRoomNoteCreateResponse] object in success case
     */
    @FormUrlEncoded
    @POST("note/create")
    suspend fun createRoomNote(
        @Field("company_id") companyId: Long,
        @Field("room_id") roomId: Long
    ): KmeRoomNoteCreateResponse

    /**
     * Renames specific note
     *
     * @param roomId id of a room
     * @param noteId id of a note
     * @param name new note name
     * @return [KmeRoomNoteRenameResponse] object in success case
     */
    @FormUrlEncoded
    @POST("note/updateNoteName")
    suspend fun renameRoomNote(
        @Field("room_id") roomId: Long,
        @Field("note_id") noteId: Long,
        @Field("newName") name: String
    ): KmeRoomNoteRenameResponse

    /**
     * Changes content in the note
     *
     * @param roomId id of a room
     * @param noteId id of a note
     * @param content
     * @param updateLogs
     * @param html
     * @return [KmeRoomNoteUpdateContentResponse] object in success case
     */
    @FormUrlEncoded
    @POST("note/updateContent")
    suspend fun updateRoomNoteContent(
        @Field("room_id") roomId: Long,
        @Field("note_id") noteId: String,
        @Field("content") content: String,
        @Field("should_update_change_log") updateLogs: Boolean = false,
        @Field("html") html: String
    ): KmeRoomNoteUpdateContentResponse

    /**
     * Delete specific note
     *
     * @param roomId id of a room
     * @param noteId id of a note
     * @return [KmeDeleteRoomNoteResponse] object in success case
     */
    @FormUrlEncoded
    @POST("note/delete")
    suspend fun deleteRoomNote(
        @Field("room_id") roomId: Long,
        @Field("note_id") noteId: Long
    ): KmeDeleteRoomNoteResponse

}
