package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeBreakoutRoomStatusType(
    @SerializedName("type") val statusType: String,
) {

    @SerializedName("active")
    ACTIVE("active"),

    @SerializedName("non-active")
    NON_ACTIVE("non-active")

}