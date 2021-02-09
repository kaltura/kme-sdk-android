package com.kme.kaltura.kmesdk.rest.response.signin

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

data class KmeRegisterResponse(
    @SerializedName("value") val value: String,
    @SerializedName("data") override val data: KmeResponseData?
) : KmeResponse()
