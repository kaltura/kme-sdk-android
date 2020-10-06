package com.kme.kaltura.kmesdk.rest.response.metadata

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse

data class GetTranslationsResponse(
    @SerializedName("data") val data: Map<String, String>?
) : KmeResponse()
