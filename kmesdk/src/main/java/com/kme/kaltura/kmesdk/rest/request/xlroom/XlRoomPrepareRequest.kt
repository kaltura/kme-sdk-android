package com.kme.kaltura.kmesdk.rest.request.xlroom

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.request.KmeRequest

class XlRoomPrepareRequest(
    @SerializedName("room_id") val roomId: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("participants") val participants: Int,
    @SerializedName("presenters") val presenters: Int,
    @SerializedName("region_id") val regionId: String,
    @SerializedName("global") val global: Boolean = false
): KmeRequest()