package com.kme.kaltura.kmesdk.ws.message.permissions

import com.google.gson.annotations.SerializedName

class WhiteboardModule(
    @SerializedName("is_moderator")
    val isModerator: String? = null
)