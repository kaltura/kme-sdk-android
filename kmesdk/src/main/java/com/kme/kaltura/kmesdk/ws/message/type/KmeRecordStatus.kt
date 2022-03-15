package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeRecordStatus(
    @SerializedName("status") val status: String
) {

    @SerializedName("STARTED", alternate = ["started"])
    STARTED("STARTED"),

    @SerializedName("INITIATED", alternate = ["initiated"])
    INITIATED("INITIATED"),

    @SerializedName("RECORDING_STARTED", alternate = ["recordingStarted"])
    RECORDING_STARTED("RECORDING_STARTED"),

    @SerializedName("RECORDING", alternate = ["recording"])
    RECORDING_IN_PROGRESS("RECORDING"),

    @SerializedName("COMPLETED", alternate = ["completed"])
    COMPLETED("COMPLETED"),

    @SerializedName("STOPPED", alternate = ["stopped"])
    STOPPED("STOPPED"),

    @SerializedName("CONVERSION_COMPLETED", alternate = ["conversionCompleted"])
    CONVERSION_COMPLETED("CONVERSION_COMPLETED"),

    @SerializedName("UPLOAD_COMPLETED", alternate = ["uploadCompleted"])
    UPLOAD_COMPLETED("UPLOAD_COMPLETED"),

    @SerializedName("RECORDER_FAILED", alternate = ["recorderFailed"])
    RECORDING_FAILED("RECORDER_FAILED")

}