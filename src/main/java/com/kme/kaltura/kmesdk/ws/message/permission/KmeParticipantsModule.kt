package com.kme.kaltura.kmesdk.ws.message.permission

import com.google.gson.annotations.SerializedName

data class KmeParticipantsModule(
    @SerializedName("is_moderator")
    val isModerator: String? = null
)