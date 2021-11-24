package com.kme.kaltura.kmesdk.ws.message.type.permissions

import com.google.gson.annotations.SerializedName

enum class KmeAudioNotificationValue(value: String) {

    @SerializedName("on")
    ON("on"),

    @SerializedName("off")
    OFF("off"),
}