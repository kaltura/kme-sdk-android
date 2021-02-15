package com.kme.kaltura.kmesdk.rest.response.room.settings

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeWhiteboardModule(
    @SerializedName("is_active")
    @Expose
    val isActive: String? = null,

    @SerializedName("visibility")
    @Expose
    val visibility: String? = null,

    @SerializedName("default_settings")
    @Expose
    val defaultSettings: KmeDefaultSettings? = null
) : Parcelable