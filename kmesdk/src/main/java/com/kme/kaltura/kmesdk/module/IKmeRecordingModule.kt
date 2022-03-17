package com.kme.kaltura.kmesdk.module

import com.kme.kaltura.kmesdk.module.IKmePeerConnectionModule.KmePeerConnectionEvents
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeCheckRecordingLicenseResponse
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeSettingsV2
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserSetting
import com.kme.kaltura.kmesdk.ws.message.type.KmeRecordStatus

/**
 * An interface for recording in the room
 */
interface IKmeRecordingModule : IKmeModule {


    /**
     * Subscribing for the room events related to recording
     * for the users and for the room itself
     */
    fun subscribe()


    /**
     * Subscribing for the recording listener
     *
     * @param listener callback with [KmeRecordingListener] for indicating main events
     */
    fun subscribeListener(
        listener: KmeRecordingListener
    )

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

    /**
     * Recording listener
     */
    interface KmeRecordingListener {

        /**
         * Callback fired always when recording status updated
         */
        fun onRecordingStatusChanged(status: KmeRecordStatus)

        /**
         * Callback fired when recording time updated
         */
        fun onRecordingTime(time: Long)
    }

}
