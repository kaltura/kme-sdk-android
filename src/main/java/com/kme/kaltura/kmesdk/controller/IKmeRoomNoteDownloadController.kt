package com.kme.kaltura.kmesdk.controller

interface IKmeRoomNoteDownloadController {

    fun downloadRoomNote(
        name: String,
        url: String,
        success: () -> Unit,
        error: (exception: Exception) -> Unit
    )

}
