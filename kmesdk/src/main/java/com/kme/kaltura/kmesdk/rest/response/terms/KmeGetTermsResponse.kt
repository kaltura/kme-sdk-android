package com.kme.kaltura.kmesdk.rest.response.terms

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

 class KmeGetTermsResponse(
    @SerializedName("data") override val data: KmeTermsData?
) : KmeResponse() {

     data class KmeTermsData(
         @SerializedName("tnc_text") val terms: String?
     ) : KmeResponseData()

     data class KmeTerm(
         @SerializedName("tnc_text") val terms: String?
    )
}
