package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeBreakoutAssignmentStatusType(
    @SerializedName("type") val statusType: String,
) {

    @SerializedName("joined")
    JOINED("joined"),

    @SerializedName("waiting")
    WAITING("waiting"),

    @SerializedName("in-progress")
    IN_PROGRESS("in-progress")

}