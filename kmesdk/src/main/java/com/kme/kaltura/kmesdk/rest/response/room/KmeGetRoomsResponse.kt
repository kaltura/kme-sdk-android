package com.kme.kaltura.kmesdk.rest.response.room

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

data class KmeGetRoomsResponse(
    @SerializedName("data") override val data: KmeRoomsData?
) : KmeResponse() {

    data class KmeRoomsData(
        @SerializedName("role") val role: String?,
        @SerializedName("rooms") val rooms: List<KmeBaseRoom>?,
        @SerializedName("total") val total: Long?
    ) : KmeResponseData()

}
