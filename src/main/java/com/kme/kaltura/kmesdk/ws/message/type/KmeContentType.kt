package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeContentType(
    @SerializedName("content_type") val contentType: String
) {

    @SerializedName("video")
    VIDEO("video"),

    @SerializedName("audio")
    AUDIO("audio"),

    @SerializedName("image")
    IMAGE("image"),

    @SerializedName("document")
    DOCUMENT("document"),

    @SerializedName("pdf")
    PDF("pdf"),

    @SerializedName("ppt")
    PPT("ppt"),

    @SerializedName("doc")
    DOC("doc"),

    @SerializedName("xls")
    XLS("xls"),

    @SerializedName("txt")
    TXT("txt"),

    @SerializedName("kaltura")
    KALTURA("kaltura"),

    @SerializedName("youtube")
    YOUTUBE("youtube"),

    @SerializedName("quiz")
    QUIZ("quiz"),

    @SerializedName("slide")
    SLIDE("slide"),

    @SerializedName("quizResult")
    QUIZ_RESULT("quizResult")

}