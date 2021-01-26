package com.kme.kaltura.kmesdk.content.whiteboard

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF

data class WhiteboardImagePath(
    val bitmap: Bitmap,
    val matrix: Matrix,
    val rectF: RectF
)