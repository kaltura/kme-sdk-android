package com.kme.kaltura.kmesdk.rest.response.signin

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

data class KmeGuestLoginResponse(
    @SerializedName("data") override val data: KmeGuestLoginData?
) : KmeResponse() {

    data class KmeGuestLoginData(
        @SerializedName("guest_id") var guestId: Long?,
        @SerializedName("url") var url: String?
    ) : KmeResponseData()

}
