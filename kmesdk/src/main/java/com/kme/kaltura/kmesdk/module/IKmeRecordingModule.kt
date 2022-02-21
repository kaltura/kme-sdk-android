package com.kme.kaltura.kmesdk.module

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeCheckRecordingLicenseResponse

/**
 * An interface for recording in the room
 */
interface IKmeRecordingModule : IKmeModule {

    /**
     * Checking recording license for the room
     *
     * @param roomId id of a room
     * @param success function to handle success result. Contains [KmeCheckRecordingLicenseResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun checkRecordingLicense(
        roomId: Long,
        success: (response: KmeCheckRecordingLicenseResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Starts recording
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param timestamp current timestamp
     * @param recordingDuration possible recording duration
     * @param timeZone current timezone
     */
    fun startRecording(
        roomId: Long,
        companyId: Long,
        timestamp: Long,
        recordingDuration: Long,
        timeZone: Long
    )

    /**
     * Stops active recording
     *
     * @param roomId id of a room
     * @param companyId id of a company
     */
    fun stopRecording(
        roomId: Long,
        companyId: Long
    )

}
