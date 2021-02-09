package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeRecordStatus(
    @SerializedName("status") val status: String
) {

    @SerializedName("RECORDING", alternate = ["recording"])
    RECORDING("RECORDING"),

    @SerializedName("completed", alternate = ["completed"])
    COMPLETED("COMPLETED")

}