package com.kme.kaltura.kmesdk.rest.response.user

import com.kme.kaltura.kmesdk.rest.response.KmeResponse

data class KmeGetUserInfoResponse(
    var data : KmeUserInfoData? = null
) : KmeResponse()
