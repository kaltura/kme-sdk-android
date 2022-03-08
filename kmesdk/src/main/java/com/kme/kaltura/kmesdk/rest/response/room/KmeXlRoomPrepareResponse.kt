package com.kme.kaltura.kmesdk.rest.response.room

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

data class KmeXlRoomPrepareResponse(
    @SerializedName("data") override val data: KmeResponseData?
) : KmeResponse()
