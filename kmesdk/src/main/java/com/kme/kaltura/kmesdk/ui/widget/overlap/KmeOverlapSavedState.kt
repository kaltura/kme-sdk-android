package com.kme.kaltura.kmesdk.ui.widget.overlap

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeOverlapSavedState(
    val posX: Float,
    val posY: Float,
    val width: Int,
    val height: Int,
    val isLandscape: Boolean
) : Parcelable