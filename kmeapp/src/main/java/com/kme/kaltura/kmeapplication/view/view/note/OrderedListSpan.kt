package com.kme.kaltura.kmeapplication.view.view.note

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.Spanned
import android.text.style.LeadingMarginSpan

class OrderedListSpan(private val index: String) : LeadingMarginSpan {

    override fun getLeadingMargin(first: Boolean): Int = 7 * DEFAULT_MARGIN

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
        val spanStart = (text as Spanned).getSpanStart(this)
        val isFirstCharacter = spanStart == start

        if (isFirstCharacter) {
            canvas.drawText(index, DEFAULT_MARGIN * 2f, baseline.toFloat(), paint)
        }
    }

    companion object {
        private const val DEFAULT_MARGIN = 10
    }

}