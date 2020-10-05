package com.kme.kaltura.kmesdk.rest.response.user

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse

data class KmeGetUserInfoResponse(
    @SerializedName("data") var data: KmeUserInfoData?
) : KmeResponse()
