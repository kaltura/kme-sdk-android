package com.kme.kaltura.kmesdk.ws.message.type.permissions

import com.google.gson.annotations.SerializedName

enum class KmeAppAccessValue(value: String) {

    @SerializedName("on")
    ON("on"),

    @SerializedName("off")
    OFF("off"),
}