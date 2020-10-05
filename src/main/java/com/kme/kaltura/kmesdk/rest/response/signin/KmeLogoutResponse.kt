package com.kme.kaltura.kmesdk.rest.response.signin

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse

data class KmeLogoutResponse(
    @SerializedName("value2") val value2: String
) : KmeResponse()
