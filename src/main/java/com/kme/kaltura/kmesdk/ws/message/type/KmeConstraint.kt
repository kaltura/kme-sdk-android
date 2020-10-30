package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeConstraint(val constraint: String) {

    @SerializedName("INCLUDE_SELF")
    INCLUDE_SELF("INCLUDE_SELF"),

    @SerializedName("MULTIPLE_EVENTS")
    MULTIPLE_EVENTS("MULTIPLE_EVENTS")

}