package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeClassMode(
    @SerializedName("class_mode") val classMode: String
) {

    @SerializedName("virtual_class")
    VIRTUAL_CLASS("virtual_class"),

    @SerializedName("webinar")
    WEBINAR("webinar")

}