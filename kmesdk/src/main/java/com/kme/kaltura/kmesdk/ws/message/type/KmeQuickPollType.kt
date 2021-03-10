package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeQuickPollType(
    @SerializedName("poll_type") val pollType: String
) {

    @SerializedName("YesNo", alternate = ["yesno"])
    YES_NO("YesNo"),

    @SerializedName("Reactions", alternate = ["reactions"])
    REACTIONS("Reactions"),

    @SerializedName("Rating", alternate = ["rating"])
    RATING("Rating"),

    @SerializedName("MultipleChoice", alternate = ["multiplechoice"])
    MULTIPLE_CHOICE("MultipleChoice")

}