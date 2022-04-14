package com.kme.kaltura.kmesdk.ui.widget.zoom

import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import kotlin.math.sqrt

class ScaleHelper {

    private var mStartTime: Long = 0
    private var mInterpolator: Interpolator? = null
    private var mToScale = 0f
    private var mDuration = 0
    private var isFinished = true

    var curScale = 0f
        private set

    var startX = 0
        private set

    var startY = 0
        private set

    fun startScale(scale: Float, toScale: Float, x: Int, y: Int, interpolator: Interpolator?) {
        mStartTime = AnimationUtils.currentAnimationTimeMillis()
        mInterpolator = interpolator
        curScale = scale
        mToScale = toScale
        startX = x
        startY = y
        var d: Float
        d = if (toScale > scale) {
            toScale / scale
        } else {
            scale / toScale
        }
        if (d > 4) {
            d = 4f
        }
        mDuration = (220 + sqrt((d * 3600).toDouble())).toInt()
        isFinished = false
    }

    /**
     * Call this when you want to know the new location. If it returns true, the
     * animation is not yet finished.
     */
    fun computeScrollOffset(): Boolean {
        if (isFinished) {
            return false
        }
        val time = AnimationUtils.currentAnimationTimeMillis()
        // Any scroller can be used for time, since they were started
        // together in scroll mode. We use X here.
        val elapsedTime = time - mStartTime
        val duration = mDuration
        if (elapsedTime < duration) {
            val q = mInterpolator!!.getInterpolation(elapsedTime / duration.toFloat())
            curScale += q * (mToScale - curScale)
        } else {
            curScale = mToScale
            isFinished = true
        }
        return true
    }
}