package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeBreakoutRoomMessageType(
    @SerializedName("messageType") val messageType: String,
) {

    @SerializedName("text")
    TEXT("text")

}