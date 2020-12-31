package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.notes.*

interface IKmeRoomNotesController : IKmeRoomNoteDownloadController {

    fun getRoomNotes(
        companyId: Long,
        roomId: Long,
        success: (response: KmeGetRoomNotesResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    fun getDownloadRoomNoteUrl(
        roomId: Long,
        noteId: Long,
        saveToFiles: Boolean,
        success: (response: KmeRoomNoteDownloadUrlResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    fun createRoomNote(
        companyId: Long,
        roomId: Long,
        success: (response: KmeRoomNoteCreateResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    fun renameRoomNote(
        roomId: Long,
        noteId: String,
        name: String,
        success: (response: KmeRoomNoteRenameResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    fun updateRoomNoteContent(
        roomId: Long,
        noteId: String,
        content: String,
        updateLogs: Boolean,
        html: String,
        success: (response: KmeRoomNoteUpdateContentResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    fun deleteRoomNote(
        roomId: Long,
        noteId: Long,
        success: (response: KmeDeleteRoomNoteResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

}
