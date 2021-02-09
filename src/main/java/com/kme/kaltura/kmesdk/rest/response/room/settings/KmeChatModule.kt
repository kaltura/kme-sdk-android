package com.kme.kaltura.kmesdk.rest.response.room.settings

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmeModuleVisibilityValue
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeChatModule(
    @SerializedName("is_active")
    @Expose
    val isActive: KmePermissionValue? = null,

    @SerializedName("visibility")
    @Expose
    val visibility: KmeModuleVisibilityValue? = null,

    @SerializedName("default_settings")
    @Expose
    var defaultSettings: KmeDefaultSettings? = null
) : Parcelable