package com.kme.kaltura.kmesdk.rest.response.signin

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

data class KmeLoginResponse(
    @SerializedName("data") override val data: KmeLoginData?
) : KmeResponse() {

    data class KmeLoginData(
        @SerializedName("user_id") var userId: Long?,
        @SerializedName("access-token") var accessToken: String?
    ) : KmeResponseData()

}
