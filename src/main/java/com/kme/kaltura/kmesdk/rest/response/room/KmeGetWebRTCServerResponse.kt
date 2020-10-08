package com.kme.kaltura.kmesdk.rest.response.room

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse

data class KmeGetWebRTCServerResponse(
    @SerializedName("data") val data: KmeWebRTCServer?
) : KmeResponse()
