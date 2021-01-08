package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeCheckRecordingLicenseResponse

interface IKmeRoomRecordingController {

    fun checkRecordingLicense(
        roomId: Long,
        success: (response: KmeCheckRecordingLicenseResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

}
