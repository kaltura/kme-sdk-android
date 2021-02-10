package com.kme.kaltura.kmeapplication.util.extensions

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import android.text.style.MetricAffectingSpan
import android.text.style.StyleSpan

fun SpannableStringBuilder.removeStyle(
    startSelection: Int,
    endSelection: Int,
    styleToRemove: Int
) {
    val spansParts = getSpanParts(this, startSelection, endSelection)
    removeStyleSpan(this, startSelection, endSelection, styleToRemove)
    restoreSpans(this, spansParts)
}

fun SpannableStringBuilder.removeOne(
    startSelection: Int,
    endSelection: Int,
    style: Class<*>
) {
    val spansParts = getSpanParts(this, startSelection, endSelection)
    removeOneSpan(this, startSelection, endSelection, style)
    restoreSpans(this, spansParts)
}

fun SpannableStringBuilder.removeAll(
    startSelection: Int,
    endSelection: Int
) {
    val spansParts = getSpanParts(this, startSelection, endSelection)
    removeAllSpans(this, startSelection, endSelection)
    restoreSpans(this, spansParts)
}

private fun removeAllSpans(
    spannable: Spannable,
    startSelection: Int,
    endSelection: Int
) {
    val spansToRemove = spannable.getSpans(
        startSelection, endSelection,
        Any::class.java
    )
    for (span in spansToRemove) {
        if (span is CharacterStyle) spannable.removeSpan(span)
    }
}

private fun removeOneSpan(
    spannable: Spannable,
    startSelection: Int,
    endSelection: Int,
    style: Class<*>
) {
    val spansToRemove = spannable.getSpans(
        startSelection, endSelection,
        Any::class.java
    )
    for (span in spansToRemove) {
        if (span.javaClass == style) spannable.removeSpan(span)
    }
}

private fun removeStyleSpan(
    spannable: Spannable,
    startSelection: Int,
    endSelection: Int,
    styleToRemove: Int
) {
    val spans = spannable.getSpans(
        startSelection, endSelection,
        MetricAffectingSpan::class.java
    )
    for (span in spans) {
        val spanUnd: Any = span.underlying
        if (spanUnd is StyleSpan) {
            val spanFlag = spannable.getSpanFlags(spanUnd)
            val stylesApplied = spanUnd.style
            val stylesToApply = stylesApplied and styleToRemove.inv()
            val spanStart = spannable.getSpanStart(span)
            val spanEnd = spannable.getSpanEnd(span)
            if (spanEnd >= 0 && spanStart >= 0) {
                spannable.removeSpan(span)
                spannable.setSpan(StyleSpan(stylesToApply), spanStart, spanEnd, spanFlag)
            }
        }
    }
}

private fun getSpanParts(
    spannable: Spannable,
    startSelection: Int,
    endSelection: Int
): ArrayList<SpanParts> {
    val spansParts = ArrayList<SpanParts>()
    val spans = spannable.getSpans(
        startSelection, endSelection,
        Any::class.java
    )
    for (span in spans) {
        if (span is CharacterStyle) {
            val spanParts = SpanParts()
            val spanStart = spannable.getSpanStart(span)
            val spanEnd = spannable.getSpanEnd(span)
            if (spanStart == startSelection && spanEnd == endSelection) continue
            spanParts.spanFlag = spannable.getSpanFlags(span)
            spanParts.part1.span = CharacterStyle.wrap(span)
            spanParts.part1.start = spanStart
            spanParts.part1.end = startSelection
            spanParts.part2.span = CharacterStyle.wrap(span)
            spanParts.part2.start = endSelection
            spanParts.part2.end = spanEnd
            spansParts.add(spanParts)
        }
    }
    return spansParts
}

private fun restoreSpans(
    spannable: Spannable,
    spansParts: ArrayList<SpanParts>
) {
    for (spanParts in spansParts) {
        if (spanParts.part1.canApply()) spannable.setSpan(
            spanParts.part1.span, spanParts.part1.start,
            spanParts.part1.end, spanParts.spanFlag
        )
        if (spanParts.part2.canApply()) spannable.setSpan(
            spanParts.part2.span, spanParts.part2.start,
            spanParts.part2.end, spanParts.spanFlag
        )
    }
}

class SpanParts internal constructor() {
    var spanFlag = 0
    var part1: Part = Part()
    var part2: Part = Part()
}

class Part {
    var span: CharacterStyle? = null
    var start = 0
    var end = 0
    fun canApply(): Boolean {
        return start < end
    }
}
