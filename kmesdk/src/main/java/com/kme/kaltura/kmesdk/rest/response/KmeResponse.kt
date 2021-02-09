package com.kme.kaltura.kmesdk.rest.response

import com.google.gson.annotations.SerializedName

abstract class KmeResponse {

    abstract val data: KmeResponseData?

    @SerializedName("status")
    val status: Status? = null

    enum class Status(val status: String) {
        @SerializedName("success")
        SUCCESS("success"),

        @SerializedName("error")
        ERROR("error")
    }

}
