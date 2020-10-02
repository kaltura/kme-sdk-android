package com.kme.kaltura.kmesdk.rest.response.room

import com.kme.kaltura.kmesdk.rest.response.KmeResponse

data class KmeGetRoomsResponse(
    val data: KmeRoomsData? = null
) : KmeResponse() {

    data class KmeRoomsData(
        val role: String? = null,
        val rooms: List<KmeRoom>? = null,
        val total: Long? = null
    )

}
