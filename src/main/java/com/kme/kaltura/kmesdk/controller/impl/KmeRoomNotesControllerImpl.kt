package com.kme.kaltura.kmesdk.controller.impl

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeGetRoomNotesResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeRoomNotesApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

class KmeRoomNotesControllerImpl : KmeController(), IKmeRoomNotesController {

    private val roomNotesApiService: KmeRoomNotesApiService by inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun getRoomNotes(
        companyId: Long,
        roomId: Long,
        success: (response: KmeGetRoomNotesResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomNotesApiService.getRoomNotes(companyId, roomId) },
                success,
                error
            )
        }
    }

}
