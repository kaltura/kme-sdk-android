package com.kme.kaltura.kmesdk.rest.response.signin

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse

data class KmeRegisterResponse(
    @SerializedName("value") val value: String
) : KmeResponse()
