package com.kme.kaltura.kmesdk

import android.content.Context
import android.graphics.*
import android.util.DisplayMetrics
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath.Cap.*


fun View?.visible() {
    if (this == null) return
    if (!isVisible()) {
        this.visibility = View.VISIBLE
    }
}

fun View?.isVisible(): Boolean {
    if (this == null) return false
    return visibility == View.VISIBLE
}

fun View?.gone() {
    if (this == null) return
    if (!isGone()) {
        this.visibility = View.GONE
    }
}

fun View?.isGone(): Boolean {
    if (this == null) return true
    return visibility == View.GONE
}

fun View?.invisible() {
    if (this == null) return
    if (!isInvisible()) {
        this.visibility = View.INVISIBLE
    }
}

fun View?.isInvisible(): Boolean {
    if (this == null) return false
    return visibility == View.INVISIBLE
}

fun TextView?.goneIfTextEmpty() {
    if (this == null) return
    if (text.isNullOrEmpty()) {
        gone()
    } else {
        visible()
    }
}

fun RadioGroup?.goneIfEmpty() {
    if (this == null) return
    if (childCount == 0) {
        gone()
    } else {
        visible()
    }
}

/*
* Converts point to dpi.
*
* @return Float The point value in dpi.
* */
internal fun ptToDp(pt: Float, context: Context): Float {
    val dpi = context.resources.displayMetrics.densityDpi.toFloat()
    val px = pt / 72 * dpi // pt is exactly 1/72 of an inch on any screen density
    return px / (dpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun dpToPx(dp: Float, context: Context): Float {
    val scale: Float = context.resources.displayMetrics.density
    return dp * scale + 0.5f
}

fun spToPx(sp: Float, context: Context): Float {
    val scale: Float = context.resources.displayMetrics.scaledDensity
    return sp * scale
}

/**
 * Getting the color value from KmeWhiteboardPath.class.
 *
 * @return Int The color value. By default, return Color.BLACK
 */
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

/**
 * Getting the color value from RGB array.
 *
 * @return Int The color value.
 */
fun FloatArray.toColor(): Int {
    return -0x1000000 or
            ((this[0] * 255.0f + 0.5f).toInt() shl 16) or
            ((this[1] * 255.0f + 0.5f).toInt() shl 8) or
            (this[2] * 255.0f + 0.5f).toInt()
}

/*
* Converts KmeWhiteboardPath.Cap to android.graphics.Paint.Cap
* */
fun KmeWhiteboardPath.Cap?.getPaintCap(): Paint.Cap {
    return when (this) {
        ROUND -> Paint.Cap.ROUND
        BUTT -> Paint.Cap.BUTT
        SQUARE -> Paint.Cap.SQUARE
        else -> Paint.Cap.ROUND
    }
}

/*
* Getting android.graphics.Paint.Style from KmeWhiteboardPath.class
* */
fun KmeWhiteboardPath?.getPaintStyle(): Paint.Style {
    return if (this?.childrenPath != null || this?.fillColor?.isNotEmpty() == true || !this?.content.isNullOrEmpty()) {
        Paint.Style.FILL
    } else {
        Paint.Style.STROKE
    }
}

/*
* Returns true if the current path is in erase mode.
* */
fun KmeWhiteboardPath.BlendMode?.isEraseMode(): Boolean {
    return KmeWhiteboardPath.BlendMode.DESTINATION_OUT == this
}

/*
* Converts the alpha channel of a color to Int value
* */
fun Float?.getPaintAlpha(): Int {
    return this?.times(255 + 0.5f)?.toInt() ?: 255
}

fun Context.getBitmap(
    @DrawableRes drawableRes: Int,
    bounds: Rect? = null,
    padding: Float = 0f
): Bitmap? {
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
    if (this == null) return null
    return if (this.width > 0 && this.height > 0) {
        val bitmap =
            Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        bitmap
    } else if (this.layoutParams.width > 0 && this.layoutParams.height > 0) {
        val specWidth = MeasureSpec.makeMeasureSpec(this.layoutParams.width, MeasureSpec.EXACTLY)
        val specHeight = MeasureSpec.makeMeasureSpec(this.layoutParams.height, MeasureSpec.EXACTLY)

        measure(specWidth, specHeight)
        layout(0, 0, this.measuredWidth, this.measuredHeight)

        if (this is ViewGroup) {
            forEach { child ->
                child.measure(specWidth, specHeight)
                child.layout(0, 0, this.measuredWidth, this.measuredHeight)
            }
        }

        val bitmap = Bitmap.createBitmap(
            this.measuredWidth,
            this.measuredHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        draw(canvas)
        bitmap
    } else {
        return null
    }
}