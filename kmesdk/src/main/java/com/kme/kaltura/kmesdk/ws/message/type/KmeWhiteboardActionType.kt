package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeWhiteboardActionType(
    @SerializedName("type") val actionType: String
) {

    @SerializedName("DRAW")
    DRAW("DRAW"),

    @SerializedName("DELETE")
    DELETE("DELETE"),

    @SerializedName("TEXT")
    TEXT("TEXT"),

    @SerializedName("CLEAR_PAGE")
    CLEAR_PAGE("CLEAR_PAGE"),

    @SerializedName("CLEAR_ALL_PAGES")
    CLEAR_ALL_PAGES("CLEAR_ALL_PAGES"),

    @SerializedName("TRANSFORM")
    TRANSFORM("TRANSFORM")

}