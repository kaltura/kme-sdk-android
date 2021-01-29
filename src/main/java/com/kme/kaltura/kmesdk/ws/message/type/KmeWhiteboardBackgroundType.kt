package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeWhiteboardBackgroundType(
    @SerializedName("background_metadata") val type: String
) {

    @SerializedName("DOTS")
    DOTS("DOTS"),

    @SerializedName("AXIS")
    AXIS("AXIS"),

    @SerializedName("GRID")
    GRID("GRID"),

    @SerializedName("LARGE_GRID")
    LARGE_GRID("LARGE_GRID"),

    @SerializedName("BLANK")
    BLANK("BLANK")



}