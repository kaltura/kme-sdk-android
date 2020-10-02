package com.kme.kaltura.kmesdk.rest.response.signin

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

data class KmeLoginResponse(
    var data : KmeLoginData? = null
) : KmeResponse()

data class KmeLoginData(
    var user_id : Long? = null,
    @SerializedName("access-token") var accessToken : String? = null
) : KmeResponseData()
