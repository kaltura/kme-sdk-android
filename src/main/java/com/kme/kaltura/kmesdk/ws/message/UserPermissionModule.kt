package com.kme.kaltura.kmesdk.ws.message

import com.google.gson.annotations.SerializedName

data class UserPermissionModule(
    @SerializedName("is_moderator") val isModerator: String
)
