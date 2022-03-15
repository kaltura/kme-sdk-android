package com.kme.kaltura.kmesdk.rest.response.room.settings

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class KmeBreakoutModule(
    @SerializedName("is_active")
    @Expose
    var isActive: String? = null,

    @SerializedName("visibility")
    @Expose
    var visibility: String? = null,

    @SerializedName("default_settings")
    @Expose
    var defaultSettings: KmeDefaultSettings? = null
) : Parcelable