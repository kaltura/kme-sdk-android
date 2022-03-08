package com.kme.kaltura.kmeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomRecordingMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomRecordingMessage.RecordingFailurePayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomRecordingMessage.RecordingStatusPayload
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class RoomRecordingViewModel(
    private val kmeSdk: KME
) : ViewModel() {

    private val recordStateInitializing = MutableLiveData<Nothing>()
    val recordStateInitializingLiveData get() = recordStateInitializing as LiveData<Nothing>

    private val recordStateReadyToStart = MutableLiveData<List<Long>>()
    val recordStateReadyToStartLiveData get() = recordStateReadyToStart as LiveData<List<Long>>

    private val recordStateReadyToStop = MutableLiveData<Nothing>()
    val recordStateReadyToStopLiveData get() = recordStateReadyToStop as LiveData<Nothing>

    private val recordStarting = MutableLiveData<Boolean>()
    val recordStartingLiveData get() = recordStarting as LiveData<Boolean>

    private val recordInitiated = MutableLiveData<Nothing>()
    val recordInitiatedLiveData get() = recordInitiated as LiveData<Nothing>

    private val record = MutableLiveData<Boolean>()
    val recordLiveData get() = record as LiveData<Boolean>

    private val recordCompleted = MutableLiveData<Nothing>()
    val recordCompletedLiveData get() = recordCompleted as LiveData<Nothing>

    private val conversionCompleted = MutableLiveData<Nothing>()
    val conversionCompletedLiveData get() = conversionCompleted as LiveData<Nothing>

    private val uploadCompleted = MutableLiveData<Nothing>()
    val uploadCompletedLiveData get() = uploadCompleted as LiveData<Nothing>

    private val recordDuration = MutableLiveData<Long>()
    val recordDurationLiveData get() = recordDuration as LiveData<Long>

    private val recordFailed = MutableLiveData<String>()
    val recordFailedLiveData get() = recordFailed as LiveData<String>

    private var uiScope = CoroutineScope(Dispatchers.Main)

    private var license: Boolean = false
    private var blockActionsForInit: Boolean = false
    private var roomId: Long = 0
    private var companyId: Long = 0

    fun setRoomData(companyId: Long, roomId: Long) {
        this.companyId = companyId
        this.roomId = roomId

        kmeSdk.roomController.recordingModule.checkRecordingLicense(
            roomId,
            success = { license = true },
            error = { license = false }
        )
    }

    fun subscribe() {
        kmeSdk.roomController.listen(
            recordingHandler,
            KmeMessageEvent.RECORDING_STARTED,
            KmeMessageEvent.RECORDING_INITIATED,
//            KmeMessageEvent.RECORDING_STARTING,
//            KmeMessageEvent.RECORDING_STOPPED,
            KmeMessageEvent.RECORDING_COMPLETED,
            KmeMessageEvent.RECORDING_CONVERSION_COMPLETED,
            KmeMessageEvent.RECORDING_UPLOAD_COMPLETED,
            KmeMessageEvent.RECORDING_STATUS,
            KmeMessageEvent.RECORDING_FAILED
        )
    }

    fun isRecordingEnabled() = license &&
            kmeSdk.roomController.webRTCServer?.roomInfo?.settingsV2?.recordingModule?.isActive == KmePermissionValue.ON

    fun askForRecordingAction() {
        if (blockActionsForInit) {
            recordStateInitializing.value = null
            return
        }

        val recording = recordLiveData.value
        if (recording == null || !recording) {
            // TODO: check max minutes we can record
            recordStateReadyToStart.value = arrayListOf(1800, 3600, 7200, 10800)
        } else {
            recordStateReadyToStop.value = null
        }
    }

    fun startRecording(recordDuration: Long) {
        blockActionsForInit = true
        kmeSdk.roomController.recordingModule.startRecording(
            roomId,
            companyId,
            System.currentTimeMillis(),
            recordDuration,
            -120
        )
    }

    fun stopRecording() {
        kmeSdk.roomController.recordingModule.stopRecording(roomId, companyId)
    }

    fun mapAvailableVariants(
        variants: List<Long>,
        prefix: String,
        minutesStr: String,
        hourStr: String,
        hoursStr: String,
    ): List<String> {
        return variants.map {
            val secondsInMinute = TimeUnit.MINUTES.toSeconds(1)
            val secondsInHour = TimeUnit.HOURS.toSeconds(1)

            val postfix = when {
                it < secondsInHour -> {
                    minutesStr
                }
                it < secondsInHour * 2 -> {
                    hourStr
                }
                else -> {
                    hoursStr
                }
            }

            var elapsed = it
            val hours = elapsed / secondsInHour
            elapsed %= secondsInHour
            val minutes = elapsed / secondsInMinute
            elapsed %= secondsInMinute

            val result = if (minutes != 0L) {
                minutes
            } else {
                hours
            }
            "$prefix $result $postfix"
        }
    }

    private val recordingHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
//                KmeMessageEvent.RECORDING_STARTING -> {
//                    blockActionsForInit = true
//                    recordStarting.value = null
//                }
                KmeMessageEvent.RECORDING_INITIATED -> {
                    recordInitiated.value = null
                }
                KmeMessageEvent.RECORDING_STARTED -> {
                    blockActionsForInit = false
                    record.value = true
                    recordDuration.value = 0
                    startTimer()
                }
//                KmeMessageEvent.RECORDING_STOPPED -> {
//                    record.value = false
//                    stopTimer()
//                }
                KmeMessageEvent.RECORDING_COMPLETED -> {
                    recordCompleted.value = null
                    record.value = false
                    stopTimer()
                }
                KmeMessageEvent.RECORDING_CONVERSION_COMPLETED -> {
                    conversionCompleted.value = null
                }
                KmeMessageEvent.RECORDING_UPLOAD_COMPLETED -> {
                    uploadCompleted.value = null
                }
                KmeMessageEvent.RECORDING_STATUS -> {
                    val msg: KmeRoomRecordingMessage<RecordingStatusPayload>? = message.toType()
//                    if (msg?.payload?.status != KmeRecordStatus.RECORDING) {
//                        return
//                    }
//                    ifNonNull(
//                        msg.payload?.timestamp,
//                        msg.payload?.userJoinTimestamp
//                    ) { startedAt, joinedAt ->
//                        record.value = true
//                        recordDuration.value = joinedAt - startedAt
//                        startTimer()
//                    }
                }
                KmeMessageEvent.RECORDING_FAILED -> {
                    val msg: KmeRoomRecordingMessage<RecordingFailurePayload>? = message.toType()
                    recordFailed.value = msg?.payload?.reason
                }
                else -> {
                }
            }
        }
    }

    private fun startTimer() {
        uiScope.launch {
            while(true) {
                recordDuration.value = recordDuration.value?.plus(1)
                delay(1000)
            }
        }
    }

    private fun stopTimer() {
        recordDuration.value = null
        uiScope.cancel()
        uiScope = CoroutineScope(Dispatchers.Main)
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }

}
