package com.kme.kaltura.kmesdk.ws.message

import com.google.gson.annotations.SerializedName

enum class KmeMessageEventType(
    @SerializedName("type") val type: String
) {

    @SerializedName("CALLBACK", alternate = ["callback"])
    CALLBACK("CALLBACK"),

    @SerializedName("VOID", alternate = ["void"])
    VOID("VOID"),

    @SerializedName("BROADCAST", alternate = ["broadcast"])
    BROADCAST("BROADCAST");

    override fun toString(): String {
        return type.toLowerCase()
    }

}