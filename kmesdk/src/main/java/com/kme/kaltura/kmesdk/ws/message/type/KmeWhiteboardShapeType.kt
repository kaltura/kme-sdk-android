package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeWhiteboardShapeType(
    @SerializedName("type") val type: String
) {

    @SerializedName("rectangle")
    RECTANGLE("rectangle")

}