package com.kme.kaltura.kmesdk

import android.content.Context
import android.graphics.*
import android.util.DisplayMetrics
import android.view.View
import androidx.core.content.ContextCompat
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

fun Context.getBitmap(drawableRes: Int, bounds: Rect? = null, padding: Float = 0f): Bitmap? {
    val drawable = ContextCompat.getDrawable(this, drawableRes)
    drawable?.let {
        val canvas = Canvas()
        val width = bounds?.width() ?: it.intrinsicWidth
        val height = bounds?.height() ?: it.intrinsicHeight
        val bitmap = Bitmap.createBitmap(
                (width + padding).toInt(),
                (height + padding).toInt(),
                Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.bounds = bounds ?: Rect(0, 0, width, height)
        drawable.draw(canvas)
        return bitmap
    }
    return null
}

fun View?.getBitmapFromView(): Bitmap? {
    if (this == null || this.width <= 0 || this.height <= 0) return null
    val bitmap =
            Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}