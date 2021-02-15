package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeSdpType(
    @SerializedName("sdp_types") val sdpType: String
) {

    @SerializedName("offer")
    OFFER("offer"),

    @SerializedName("answer")
    ANSWER("answer")

}