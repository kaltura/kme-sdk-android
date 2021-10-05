package com.kme.kaltura.kmesdk.rest.response.terms

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

data class KmeGetTermsResponse(
    @SerializedName("data") override val data: KmeTermsData?
) : KmeResponse() {

    data class KmeTermsData(
        @SerializedName("terms") val terms: String?
    ) : KmeResponseData()

}
