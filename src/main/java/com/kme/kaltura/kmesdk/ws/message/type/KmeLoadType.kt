package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeLoadType(
    @SerializedName("load_type") val loadType: String
) {

    @SerializedName("PARTIAL_LOAD")
    PARTIAL_LOAD("PARTIAL_LOAD")

}