package com.kme.kaltura.kmesdk.rest.response

import com.google.gson.annotations.SerializedName

open class KmeResponse {

    val status: Status? = null
    val code : Int? = null

    enum class Status(val status: String) {
        @SerializedName("success")
        SUCCESS("success"),

        @SerializedName("error")
        ERROR("error")
    }

}
