package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeWhiteboardToolType(
    @SerializedName("tool") val tool: String
) {

    @SerializedName("SHAPE_TOOL")
    SHAPE_TOOL("SHAPE_TOOL"),

    @SerializedName("LINE_TOOL")
    LINE_TOOL("LINE_TOOL"),

    @SerializedName("PENCIL")
    PENCIL("PENCIL"),

    @SerializedName("LASER")
    LASER("LASER"),

    @SerializedName("TEXT")
    TEXT("TEXT")
}