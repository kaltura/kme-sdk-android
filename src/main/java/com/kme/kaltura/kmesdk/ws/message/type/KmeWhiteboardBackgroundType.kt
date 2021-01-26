package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeWhiteboardBackgroundType(
    @SerializedName("background_metadata") val type: String
) {

    @SerializedName("DOTS")
    DOTS("DOTS")

}