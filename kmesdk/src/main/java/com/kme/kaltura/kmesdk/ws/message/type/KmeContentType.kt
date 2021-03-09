package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeContentType(
    @SerializedName("content_type") val contentType: String
) {

    @SerializedName("conference_view")
    CONFERENCE_VIEW("conference_view"),

    @SerializedName("video")
    VIDEO("video"),

    @SerializedName("whiteboard")
    WHITEBOARD("whiteboard"),

    @SerializedName("audio")
    AUDIO("audio"),

    @SerializedName("image")
    IMAGE("image"),

    @SerializedName("document")
    DOCUMENT("document"),

    @SerializedName("kaltura")
    KALTURA("kaltura"),

    @SerializedName("youtube")
    YOUTUBE("youtube"),

    @SerializedName("quiz")
    QUIZ("quiz"),

    @SerializedName("slides")
    SLIDES("slides"),

    @SerializedName("quizResult")
    QUIZ_RESULT("quizResult"),

    @SerializedName("desktop_share")
    DESKTOP_SHARE("desktop_share")

}