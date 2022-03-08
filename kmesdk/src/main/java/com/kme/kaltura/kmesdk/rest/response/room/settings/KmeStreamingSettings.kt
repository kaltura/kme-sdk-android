package com.kme.kaltura.kmesdk.rest.response.room.settings

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class KmeStreamingSettings (
    @SerializedName("video")
    @Expose
    var video: KmeVideo? = null
) : Parcelable