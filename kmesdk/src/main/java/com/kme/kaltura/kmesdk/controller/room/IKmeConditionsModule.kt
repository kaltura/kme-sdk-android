package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeGetRoomNotesResponse

/**
 * An interface for actions with notes
 */
interface IKmeConditionsModule : IKmeModule {

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

}
