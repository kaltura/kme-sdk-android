package com.kme.kaltura.kmesdk.rest.request.xlroom

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.request.KmeRequest

class XlRoomLaunchRequest(
    @SerializedName("room_id") val roomId: Long
): KmeRequest()