package com.kme.kaltura.kmesdk.ws.message.permission

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeNotesModule(
    @SerializedName("is_moderator")
    val isModerator: String? = null
) : Parcelable