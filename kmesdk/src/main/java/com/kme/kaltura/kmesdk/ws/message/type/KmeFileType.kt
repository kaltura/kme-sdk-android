package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeFileType(
    @SerializedName("content_type") val contentType: String
) {

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

    @SerializedName("quiz")
    QUIZ("quiz"),

    @SerializedName("slide")
    SLIDE("slide"),

    @SerializedName("quizResult")
    QUIZ_RESULT("quizResult")

}