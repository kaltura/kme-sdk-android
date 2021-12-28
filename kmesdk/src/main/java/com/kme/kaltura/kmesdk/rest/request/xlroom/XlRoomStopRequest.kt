package com.kme.kaltura.kmesdk.rest.request.xlroom

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.request.KmeRequest

class XlRoomStopRequest(
    @SerializedName("room_id") val roomId: Long,
    @SerializedName("company_id") val companyId: Long
): KmeRequest()