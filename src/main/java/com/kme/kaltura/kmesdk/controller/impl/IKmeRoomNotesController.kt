package com.kme.kaltura.kmesdk.controller.impl

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeGetRoomNotesResponse

interface IKmeRoomNotesController {

    fun getRoomNotes(
        companyId: Long,
        roomId: Long,
        success: (response: KmeGetRoomNotesResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

}
