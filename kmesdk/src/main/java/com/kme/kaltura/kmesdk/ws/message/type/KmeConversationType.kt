package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeConversationType(
    @SerializedName("conversation_type") val conversationType: String
) {

    @SerializedName("PRIVATE")
    PRIVATE("PRIVATE"),

    @SerializedName("PUBLIC")
    PUBLIC("PUBLIC"),

    @SerializedName("MODERATORS")
    MODERATORS("MODERATORS"),

    @SerializedName("QNA")
    QNA("QNA")

}