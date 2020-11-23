package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeUserRole(
   @SerializedName("user_role") val userRole: String
) {

    @SerializedName("instructor", alternate = ["INSTRUCTOR"])
    INSTRUCTOR("instructor"),

    @SerializedName("admin", alternate = ["ADMIN"])
    ADMIN("admin"),

    @SerializedName("student", alternate = ["STUDENT"])
    STUDENT("student"),

    @SerializedName("guest", alternate = ["GUEST"])
    GUEST("guest"),

    @SerializedName("owner", alternate = ["OWNER"])
    OWNER("owner")

}