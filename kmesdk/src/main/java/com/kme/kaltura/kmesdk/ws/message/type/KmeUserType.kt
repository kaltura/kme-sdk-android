package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeUserType(
   @SerializedName("user_type") val userType: String
) {

    @SerializedName("recorder", alternate = ["RECORDER"])
    RECORDER("recorder"),

    @SerializedName("user", alternate = ["USER"])
    USER("user")

}