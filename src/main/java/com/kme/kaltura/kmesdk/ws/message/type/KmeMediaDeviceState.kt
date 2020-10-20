package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeMediaDeviceState(
   val mediaDeviceState: String
) {

    @SerializedName("DISABLED_LIVE")
    DISABLED_LIVE("DISABLED_LIVE"),

    @SerializedName("DISABLED_UNLIVE")
    DISABLED_UNLIVE("DISABLED_UNLIVE"),

    @SerializedName("UNLIVE")
    UNLIVE("UNLIVE"),

    @SerializedName("LIVE")
    LIVE("LIVE"),

    @SerializedName("MUTED_LIVE")
    MUTED_LIVE("MUTED_LIVE"),

    @SerializedName("MUTED_UNLIVE")
    MUTED_UNLIVE("MUTED_UNLIVE"),

    @SerializedName("DISABLED_NO_PERMISSIONS")
    DISABLED_NO_PERMISSIONS("DISABLED_NO_PERMISSIONS"),

    @SerializedName("LIVE_ERROR")
    LIVE_ERROR("LIVE_ERROR")

}