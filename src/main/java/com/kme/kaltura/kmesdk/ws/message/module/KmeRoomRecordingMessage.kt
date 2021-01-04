package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage

class KmeRoomRecordingMessage<T : KmeRoomRecordingMessage.RecordingPayload> : KmeMessage<T>() {

    data class RecordingStartPayload(
        @SerializedName("recordingDuration") val recordingDuration: Long,
        @SerializedName("timestamp") val timestamp: Long,
        @SerializedName("timeZone") val timeZone: Long,
        @SerializedName("room_id") val roomId: Long,
        @SerializedName("company_id") val companyId: Long
    ) : RecordingTxPayload()

    data class RecordingStopPayload(
        @SerializedName("room_id") val roomId: Long,
        @SerializedName("company_id") val companyId: Long
    ) : RecordingTxPayload()

    data class RecordingStartingPayload(
        @SerializedName("timestamp") val timestamp: Long
    ) : RecordingRxPayload()

    data class RecordingInitiatedPayload(
        @SerializedName("timestamp") val timestamp: Long
    ) : RecordingRxPayload()

    data class RecordingStartedPayload(
        @SerializedName("recordingDuration") val recordingDuration: Long,
        @SerializedName("timestamp") val timestamp: Long,
        @SerializedName("responseCode") val responseCode: Int
    ) : RecordingRxPayload()

    data class RecordingStoppedPayload(
        @SerializedName("responseCode") val responseCode: Int
    ) : RecordingRxPayload()

    data class RecordingCompletedPayload(
        @SerializedName("responseCode") val responseCode: Int
    ) : RecordingRxPayload()

    data class RecordingConversionCompletedPayload(
        @SerializedName("responseCode") val responseCode: Int
    ) : RecordingRxPayload()

    data class RecordingUploadCompletedPayload(
        @SerializedName("responseCode") val responseCode: Int
    ) : RecordingRxPayload()

    data class RecordingFailurePayload(
        @SerializedName("responseCode") val responseCode: Int,
        @SerializedName("reason") val reason: String,
    ) : RecordingRxPayload()

    open class RecordingTxPayload : RecordingPayload()
    open class RecordingRxPayload : RecordingPayload()
    open class RecordingPayload : KmeMessage.Payload()

}
