package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.notes.*

/**
 * An interface for actions with notes
 */
interface IKmeNoteModule : IKmeNoteDownloadModule {

    /**
     * Getting all notes for specific room
     *
     * @param companyId id of a company
     * @param roomId id of a room
     * @param success function to handle success result. Contains [KmeGetRoomNotesResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun getRoomNotes(
        companyId: Long,
        roomId: Long,
        success: (response: KmeGetRoomNotesResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Getting specific note
     *
     * @param roomId id of a room
     * @param noteId id of a note
     * @param success function to handle success result. Contains [KmeGetRoomNotesResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun getRoomNote(
        roomId: Long,
        noteId: Long,
        success: (response: KmeGetRoomNoteResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Getting an url for download note as pdf file
     *
     * @param roomId id of a room
     * @param noteId id of a note
     * @param saveToFiles
     * @param success function to handle success result. Contains [KmeRoomNoteDownloadUrlResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun getDownloadRoomNoteUrl(
        roomId: Long,
        noteId: Long,
        saveToFiles: Boolean,
        success: (response: KmeRoomNoteDownloadUrlResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Creates a new note in the room
     *
     * @param companyId id of a company
     * @param roomId id of a room
     * @param success function to handle success result. Contains [KmeRoomNoteCreateResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun createRoomNote(
        companyId: Long,
        roomId: Long,
        success: (response: KmeRoomNoteCreateResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Renames specific note
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param noteId id of a note
     * @param name new name for the note
     * @param success function to handle success result. Contains [KmeRoomNoteRenameResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun renameRoomNote(
        roomId: Long,
        companyId: Long,
        noteId: Long,
        name: String,
        success: (response: KmeRoomNoteRenameResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Changes content in the note
     *
     * @param roomId id of a room
     * @param noteId id of a note
     * @param content
     * @param updateLogs
     * @param html
     * @param success function to handle success result. Contains [KmeRoomNoteUpdateContentResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun updateRoomNoteContent(
        roomId: Long,
        noteId: String,
        content: String,
        updateLogs: Boolean,
        html: String,
        success: (response: KmeRoomNoteUpdateContentResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Propagate note to all participants
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param noteId id of a note
     * @param name name for the note
     */
    fun broadcastNote(
        roomId: Long,
        companyId: Long,
        noteId: Long,
        name: String
    )

    /**
     * Subscribes/unsubscribes to the note changes
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param noteId id of a note
     * @param isSubscribeToNote subscription flag
     */
    fun subscribeToNoteChanges(
        roomId: Long,
        companyId: Long,
        noteId: Long,
        isSubscribeToNote: Boolean
    )

    /**
     * Deletes specific note
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param noteId id of a note
     * @param success function to handle success result. Contains [KmeDeleteRoomNoteResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun deleteRoomNote(
        roomId: Long,
        companyId: Long,
        noteId: Long,
        success: (response: KmeDeleteRoomNoteResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

}
