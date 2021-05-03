package com.kme.kaltura.kmesdk.rest.response.metadata

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

class KeepAliveResponse(
    @SerializedName("data") override val data: KmeResponseData?
) : KmeResponse()