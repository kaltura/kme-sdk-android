package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeMediaDeviceState(
    val mediaDeviceState: String
) {

    @SerializedName("DISABLED_LIVE")
    DISABLED_LIVE("DISABLED_LIVE"),

    @SerializedName("DISABLED_UNLIVE")
    DISABLED_UNLIVE("DISABLED_UNLIVE"),

    @SerializedName("UNLIVE")
    UNLIVE("UNLIVE"),

    @SerializedName("LIVE")
    LIVE("LIVE"),

    @SerializedName("MUTED_LIVE")
    MUTED_LIVE("MUTED_LIVE"),

    @SerializedName("MUTED_UNLIVE")
    MUTED_UNLIVE("MUTED_UNLIVE"),

    @SerializedName("DISABLED_NO_PERMISSIONS")
    DISABLED_NO_PERMISSIONS("DISABLED_NO_PERMISSIONS"),

    @SerializedName("DISABLED")
    DISABLED("DISABLED"),

    @SerializedName("LIVE_INIT")
    LIVE_INIT("LIVE_INIT"),

    @SerializedName("LIVE_SUCCESS")
    LIVE_SUCCESS("LIVE_SUCCESS"),

    @SerializedName("LIVE_ERROR")
    LIVE_ERROR("LIVE_ERROR");

    inline fun <reified T : Enum<T>> value(type: String?): T? {
        if (type == null) return null
        return try {
            java.lang.Enum.valueOf(T::class.java, type)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

}