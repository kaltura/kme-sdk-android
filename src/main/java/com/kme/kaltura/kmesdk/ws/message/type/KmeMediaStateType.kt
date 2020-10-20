package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeMediaStateType(
   @SerializedName("media_state_type") val mediaStateType: String
) {

    @SerializedName("mic_state")
    MIC("mic_state"),

    @SerializedName("webcam_state")
    WEBCAM("webcam_state"),

    @SerializedName("live_media_state")
    LIVE_MEDIA("live_media_state")

}