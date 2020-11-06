package com.kme.kaltura.kmesdk.rest.response.room.settings

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class KmeCloseCaptioningModule(
    @SerializedName("is_active")
    @Expose
    val isActive: String? = null,

    @SerializedName("visibility")
    @Expose
    val visibility: String? = null,

    @SerializedName("default_settings")
    @Expose
    val defaultSettings: KmeDefaultSettings? = null
)