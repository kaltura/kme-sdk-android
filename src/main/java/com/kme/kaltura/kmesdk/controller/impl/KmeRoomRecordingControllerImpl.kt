package com.kme.kaltura.kmesdk.controller.impl

import com.kme.kaltura.kmesdk.controller.IKmeRoomRecordingController
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeCheckRecordingLicenseResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeRoomRecordingApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * An implementation for recording in the room
 */
class KmeRoomRecordingControllerImpl : KmeController(), IKmeRoomRecordingController {

    private val roomRecordingApiService: KmeRoomRecordingApiService by inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    /**
     * Checking recording license for the room
     */
    override fun checkRecordingLicense(
        roomId: Long,
        success: (response: KmeCheckRecordingLicenseResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomRecordingApiService.heckRecordingLicense(roomId) },
                success,
                error
            )
        }
    }

}
