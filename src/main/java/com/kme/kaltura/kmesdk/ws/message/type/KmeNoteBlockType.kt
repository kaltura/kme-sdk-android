package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeNoteBlockType(
    @SerializedName("type") val type: String
) {

    @SerializedName("unstyled")
    UNSTYLED("unstyled"),

    @SerializedName("ordered-list-item")
    ORDERED_LIST("ordered-list-item"),

    @SerializedName("unordered-list-item")
    UNORDERED_LIST("unordered-list-item")

}