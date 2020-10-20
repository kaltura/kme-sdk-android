package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeUserRole(
   @SerializedName("user_role") val userRole: String
) {

    @SerializedName("INSTRUCTOR")
    INSTRUCTOR("INSTRUCTOR"),

    @SerializedName("ADMIN")
    ADMIN("ADMIN"),

    @SerializedName("OWNER")
    OWNER("OWNER")

}