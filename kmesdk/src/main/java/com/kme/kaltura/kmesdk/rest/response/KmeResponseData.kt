package com.kme.kaltura.kmesdk.rest.response

import com.google.gson.annotations.SerializedName

open class KmeResponseData {
    @SerializedName("message")
    val message: String? = null
    @SerializedName("code")
    val code: Int? = null
}
