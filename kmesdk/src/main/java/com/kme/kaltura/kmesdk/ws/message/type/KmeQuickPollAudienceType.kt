package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmeQuickPollAudienceType(
    @SerializedName("target_audience") val targetAudience: String
) {

    @SerializedName("NonModerators", alternate = ["nonmoderators"])
    NON_MODERATORS("NonModerators"),

    @SerializedName("All", alternate = ["all"])
    All("All")

}