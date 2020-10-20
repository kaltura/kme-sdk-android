package com.kme.kaltura.kmesdk.ws.message.permission

import com.google.gson.annotations.SerializedName

class KmeWhiteboardModule(
    @SerializedName("is_moderator")
    val isModerator: String? = null
)