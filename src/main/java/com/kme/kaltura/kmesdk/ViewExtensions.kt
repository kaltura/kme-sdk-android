package com.kme.kaltura.kmesdk

import android.content.Context
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.DisplayMetrics
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath.Cap.*


internal fun ptToDp(pt: Float, context: Context): Float {
    val dpi = context.resources.displayMetrics.densityDpi.toFloat()
    val px = pt / 72 * dpi // pt is exactly 1/72 of an inch on any screen density
    return px / (dpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun FloatArray?.toColor(): Int {
    return if (this != null && this.isNotEmpty() && size == 3) {
        -0x1000000 or
                ((this[0] * 255.0f + 0.5f).toInt() shl 16) or
                ((this[1] * 255.0f + 0.5f).toInt() shl 8) or
                (this[2] * 255.0f + 0.5f).toInt()
    } else {
        -0x1000000
    }
}

fun KmeWhiteboardPath.Cap?.toPaintCap(): Paint.Cap {
    return when (this) {
        ROUND -> Paint.Cap.ROUND
        BUTT -> Paint.Cap.BUTT
        SQUARE -> Paint.Cap.SQUARE
        else -> Paint.Cap.ROUND
    }
}

fun KmeWhiteboardPath.BlendMode?.getPorterDuffMode(): PorterDuff.Mode? {
    return when (this) {
//        KmeWhiteboardPath.BlendMode.MULTIPLY -> PorterDuff.Mode.MULTIPLY
        KmeWhiteboardPath.BlendMode.DESTINATION_OUT -> PorterDuff.Mode.SRC_OUT
        else -> null
    }
}

fun Float?.toPaintAlpha(): Int {
    return this?.times(255 + 0.5f)?.toInt() ?: 255
}