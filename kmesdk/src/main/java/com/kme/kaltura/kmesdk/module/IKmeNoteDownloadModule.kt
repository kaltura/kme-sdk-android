package com.kme.kaltura.kmesdk.module

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeGetRoomNotesResponse

/**
 * An interface for download notes
 */
interface IKmeNoteDownloadModule : IKmeModule {

    /**
     * Download specific note as pdf file
     *
     * @param name name of a note
     * @param url url location of a note
     * @param success function to handle success result. Contains [KmeGetRoomNotesResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun downloadRoomNote(
        name: String,
        url: String,
        success: () -> Unit,
        error: (exception: Exception) -> Unit
    )

}
