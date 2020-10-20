package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeLiveMediaState(
   @SerializedName("live_media_state") val liveMediaState: String
) {

    @SerializedName("DISABLED")
    DISABLED("DISABLED"),

    @SerializedName("UNLIVE")
    UNLIVE("UNLIVE"),

    @SerializedName("LIVE_INIT")
    LIVE_INIT("LIVE_INIT"),

    @SerializedName("LIVE_ERROR")
    LIVE_ERROR("LIVE_ERROR"),

    @SerializedName("LIVE_SUCCESS")
    LIVE_SUCCESS("LIVE_SUCCESS")

}