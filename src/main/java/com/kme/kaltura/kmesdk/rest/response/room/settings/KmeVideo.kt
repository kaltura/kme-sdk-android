package com.kme.kaltura.kmesdk.rest.response.room.settings

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeVideo(
    @SerializedName("frameRate")
    @Expose
    var frameRate: KmeFrameRate? = null
) : Parcelable