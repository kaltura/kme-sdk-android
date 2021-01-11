package com.kme.kaltura.kmesdk

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.DisplayMetrics
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath.Cap.*


internal fun ptToDp(pt: Float, context: Context): Float {
    val dpi = context.resources.displayMetrics.densityDpi.toFloat()
    val px = pt / 72 * dpi // pt is exactly 1/72 of an inch on any screen density
    return px / (dpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun KmeWhiteboardPath?.getPaintColor(): Int {
    val childrenColor = this?.childrenPath?.fillColor
    return when {
        childrenColor?.size == 3 -> {
            childrenColor.toColor()
        }
        this?.fillColor?.size == 3 -> {
            this.fillColor.toColor()
        }
        this?.strokeColor?.size == 3 -> {
            this.strokeColor.toColor()
        }
        else -> {
            Color.BLACK
        }
    }
}

fun FloatArray.toColor(): Int {
    return -0x1000000 or
            ((this[0] * 255.0f + 0.5f).toInt() shl 16) or
            ((this[1] * 255.0f + 0.5f).toInt() shl 8) or
            (this[2] * 255.0f + 0.5f).toInt()
}

fun KmeWhiteboardPath.Cap?.getPaintCap(): Paint.Cap {
    return when (this) {
        ROUND -> Paint.Cap.ROUND
        BUTT -> Paint.Cap.BUTT
        SQUARE -> Paint.Cap.SQUARE
        else -> Paint.Cap.ROUND
    }
}

fun KmeWhiteboardPath?.getPaintStyle(): Paint.Style {
    return if (this?.childrenPath != null || this?.fillColor?.isNotEmpty() == true || !this?.content.isNullOrEmpty()) {
        Paint.Style.FILL
    } else {
        Paint.Style.STROKE
    }
}

fun KmeWhiteboardPath.BlendMode?.isEraseMode(): Boolean {
    return KmeWhiteboardPath.BlendMode.DESTINATION_OUT == this
}

fun Float?.getPaintAlpha(): Int {
    return this?.times(255 + 0.5f)?.toInt() ?: 255
}