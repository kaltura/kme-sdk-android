package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeRecordingModule
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeCheckRecordingLicenseResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeRoomRecordingApiService
import com.kme.kaltura.kmesdk.util.messages.buildStartRoomRecordingMessage
import com.kme.kaltura.kmesdk.util.messages.buildStopRoomRecordingMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * An implementation for recording in the room
 */
class KmeRecordingModuleImpl : KmeController(), IKmeRecordingModule {

    private val roomRecordingApiService: KmeRoomRecordingApiService by inject()
    private val roomController: IKmeRoomController by scopedInject()

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

    /**
     * Starts recording
     */
    override fun startRecording(
        roomId: Long,
        companyId: Long,
        timestamp: Long,
        recordingDuration: Long,
        timeZone: Long,
    ) {
        roomController.send(
            buildStartRoomRecordingMessage(
                roomId,
                companyId,
                timestamp,
                recordingDuration,
                timeZone
            ))
    }

    /**
     * Stops active recording
     */
    override fun stopRecording(roomId: Long, companyId: Long) {
        roomController.send(
            buildStopRoomRecordingMessage(
                roomId,
                companyId
            ))
    }

}
