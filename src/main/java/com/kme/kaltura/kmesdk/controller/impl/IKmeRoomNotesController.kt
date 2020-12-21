package com.kme.kaltura.kmesdk.controller.impl

import com.kme.kaltura.kmesdk.controller.IKmeRoomNoteDownloadController
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeDeleteRoomNoteResponse
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeGetRoomNotesResponse
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeRoomNoteCreateResponse
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeRoomNoteDownloadUrlResponse

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

    fun deleteRoomNote(
        roomId: Long,
        noteId: Long,
        success: (response: KmeDeleteRoomNoteResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

}
