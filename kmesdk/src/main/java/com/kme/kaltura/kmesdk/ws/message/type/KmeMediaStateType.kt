package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeMediaStateType(
   @SerializedName("media_state_type") val mediaStateType: String
) {

    @SerializedName("mic_state", alternate = ["MIC_STATE"])
    MIC("mic_state"),

    @SerializedName("webcam_state", alternate = ["WEBCAM_STATE"])
    WEBCAM("webcam_state"),

    @SerializedName("live_media_state", alternate = ["LIVE_MEDIA_STATE"])
    LIVE_MEDIA("live_media_state")

}