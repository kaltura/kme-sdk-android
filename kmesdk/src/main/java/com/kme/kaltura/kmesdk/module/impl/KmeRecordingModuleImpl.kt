package com.kme.kaltura.kmesdk.module.impl

import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.module.IKmeRecordingModule
import com.kme.kaltura.kmesdk.controller.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.ifNonNull
import com.kme.kaltura.kmesdk.module.IKmePeerConnectionModule
import com.kme.kaltura.kmesdk.module.IKmeRecordingModule.KmeRecordingListener
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeCheckRecordingLicenseResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeRoomRecordingApiService
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.messages.buildStartRoomRecordingMessage
import com.kme.kaltura.kmesdk.util.messages.buildStopRoomRecordingMessage
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomRecordingMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeRecordStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * An implementation for recording in the room
 */
class KmeRecordingModuleImpl : KmeController(), IKmeRecordingModule {

    private val roomRecordingApiService: KmeRoomRecordingApiService by inject()
    private val userController: IKmeUserController by inject()
    private val roomController: IKmeRoomController by scopedInject()

    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var listener: KmeRecordingListener? = null

    private var savedMessageEvent: KmeMessage<KmeMessage.Payload>? = null

    /**
     * Subscribing for the room events related to recording
     * for the users and for the room itself
     */
    override fun subscribe() {
        roomController.listen(
            recordingHandler,
            KmeMessageEvent.RECORDING_STARTED,
            KmeMessageEvent.RECORDING_INITIATED,
            KmeMessageEvent.RECORDING_RECEIVED_START,
            KmeMessageEvent.RECORDING_RECEIVED_STOP,
            KmeMessageEvent.RECORDING_COMPLETED,
            KmeMessageEvent.RECORDING_CONVERSION_COMPLETED,
            KmeMessageEvent.RECORDING_UPLOAD_COMPLETED,
            KmeMessageEvent.RECORDING_STATUS,
            KmeMessageEvent.RECORDING_FAILED
        )
    }

    /**
     * Subscribing for the recording listener
     */
    override fun subscribeListener(listener: KmeRecordingListener) {
        this.listener = listener
        savedMessageEvent?.let {
            recordingMessages(it)
            savedMessageEvent == null
        }
    }

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
            )
        )
    }

    /**
     * Stops active recording
     */
    override fun stopRecording(roomId: Long, companyId: Long) {
        roomController.send(
            buildStopRoomRecordingMessage(
                roomId,
                companyId
            )
        )
    }


    private val recordingHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            if (listener != null) {
                recordingMessages(message)
            } else {
                savedMessageEvent = message
            }
        }
    }

    private fun recordingMessages(message: KmeMessage<KmeMessage.Payload>) {
        when (message.name) {
            KmeMessageEvent.RECORDING_RECEIVED_START -> {
                listener?.onRecordingStatusChanged(KmeRecordStatus.STARTED)
            }
            KmeMessageEvent.RECORDING_INITIATED -> {
                listener?.onRecordingStatusChanged(KmeRecordStatus.INITIATED)
            }
            KmeMessageEvent.RECORDING_STARTED -> {
                listener?.onRecordingStatusChanged(KmeRecordStatus.RECORDING_STARTED)
            }
            KmeMessageEvent.RECORDING_RECEIVED_STOP -> {
                listener?.onRecordingStatusChanged(KmeRecordStatus.STOPPED)
            }
            KmeMessageEvent.RECORDING_COMPLETED -> {
                listener?.onRecordingStatusChanged(KmeRecordStatus.COMPLETED)
            }
            KmeMessageEvent.RECORDING_CONVERSION_COMPLETED -> {
                if (userController.isModerator()) {
                    listener?.onRecordingStatusChanged(KmeRecordStatus.CONVERSION_COMPLETED)
                }
            }
            KmeMessageEvent.RECORDING_UPLOAD_COMPLETED -> {
                if (userController.isModerator()) {
                    listener?.onRecordingStatusChanged(KmeRecordStatus.UPLOAD_COMPLETED)
                }
            }
            KmeMessageEvent.RECORDING_STATUS -> {
                val msg: KmeRoomRecordingMessage<KmeRoomRecordingMessage.RecordingStatusPayload>? =
                    message.toType()
                msg?.payload?.status?.let { status ->
                    when (status) {
                        KmeRecordStatus.RECORDING_IN_PROGRESS -> {
                            listener?.onRecordingStatusChanged(KmeRecordStatus.RECORDING_IN_PROGRESS)

                            ifNonNull(
                                msg.payload?.timestamp,
                                msg.payload?.userJoinTimestamp
                            ) { startedAt, joinedAt ->
                                listener?.onRecordingTime(joinedAt - startedAt)
                            }
                        }
                        else -> {
                        }
                    }
                }
            }
            KmeMessageEvent.RECORDING_FAILED -> {
                listener?.onRecordingStatusChanged(KmeRecordStatus.RECORDING_FAILED)
            }
            else -> {
            }
        }
    }
}
