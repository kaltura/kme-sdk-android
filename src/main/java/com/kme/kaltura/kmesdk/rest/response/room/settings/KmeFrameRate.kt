package com.kme.kaltura.kmesdk.rest.response.room.settings

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeFrameRate (
    @SerializedName("min")
    @Expose
    var min: Double? = null,

    @SerializedName("max")
    @Expose
    var max: Double? = null,

    @SerializedName("ideal")
    @Expose
    var ideal: Double? = null
) : Parcelable