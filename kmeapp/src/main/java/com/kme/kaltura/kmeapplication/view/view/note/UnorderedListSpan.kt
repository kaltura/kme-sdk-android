package com.kme.kaltura.kmeapplication.view.view.note

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.Spanned
import android.text.style.LeadingMarginSpan

class UnorderedListSpan : LeadingMarginSpan {

    override fun getLeadingMargin(first: Boolean): Int = 7 * BULLET_RADIUS.toInt()

    override fun drawLeadingMargin(
        canvas: Canvas,
        paint: Paint,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout
    ) {
        if ((text as Spanned).getSpanStart(this) == start) {
            val xPosition = 3 * BULLET_RADIUS
            val yPosition = (top + bottom) / 2f

            canvas.drawCircle(
                xPosition,
                yPosition,
                BULLET_RADIUS,
                Paint()
            )
        }
    }

    companion object {
        private const val BULLET_RADIUS = 10f
    }

}