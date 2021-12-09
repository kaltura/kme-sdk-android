package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeXlRoomStatus(
    @SerializedName("XLRoomStatus") val status: String
) {

    @SerializedName("initiating", alternate = ["INITIATING"])
    INITIATING("initiating"),

    @SerializedName("ready", alternate = ["READY"])
    READY("ready"),

    @SerializedName("active", alternate = ["ACTIVE"])
    ACTIVE("active"),

    @SerializedName("aborted", alternate = ["ABORTED"])
    ABORTED("aborted"),

    @SerializedName("failed", alternate = ["FAILED"])
    FAILED("failed"),

    @SerializedName("finished", alternate = ["FINISHED"])
    FINISHED("finished")

}
