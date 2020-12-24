package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeNoteStyle(
    @SerializedName("style") val style: String
) {

    @SerializedName("ITALIC")
    ITALIC("ITALIC"),

    @SerializedName("BOLD")
    BOLD("BOLD"),

    @SerializedName("UNDERLINE")
    UNDERLINE("UNDERLINE")

}