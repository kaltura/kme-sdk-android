package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmePlatformType(
   @SerializedName("platform_type") val userRole: String
) {

    @SerializedName("desktop")
    DESKTOP("desktop"),

    @SerializedName("mobile")
    MOBILE("mobile");

    override fun toString(): String {
        return userRole
    }}