package com.kme.kaltura.kmesdk.ui.widget.overlap

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class KmeOverlapSavedState(
    val posX: Float,
    val posY: Float,
    val width: Int,
    val height: Int,
    val isLandscape: Boolean
) : Parcelable