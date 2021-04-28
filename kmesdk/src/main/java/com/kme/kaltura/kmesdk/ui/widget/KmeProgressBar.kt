package com.kme.kaltura.kmesdk.ui.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.dpToPx
import com.kme.kaltura.kmesdk.getBitmap
import com.kme.kaltura.kmesdk.spToPx

class KmeProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var maxProgress = 100
        private set
    var currentProgress = 0
        private set

    var reachedBarColor = 0
        private set
    var textColor = 0
        private set

    var textSize = 0f
        private set

    var reachedBarHeight = 0f
        private set

    private var icon = R.drawable.ic_poll_star
    private var initProgress = 0

    var prefix = ""
        private set

    private var iconBitmap: Bitmap? = null

    private val suffix = "%"

    private var drawTextWidth = 0f

    private var drawTextStart = 0f

    private var drawTextEnd = 0f

    private var currentDrawText: String = ""

    private var reachedBarPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }

    private val reachedRectF = RectF(0f, 0f, 0f, 0f)
    private val iconBounds = Rect(0, 0, dpToPx(30f, context).toInt(), dpToPx(30f, context).toInt())

    private var offset = 0f

    private var progressAnimator = ValueAnimator.ofInt(0, 100)

    private var animationProgress = 100

    init {
        val attributes = context.theme.obtainStyledAttributes(
            attrs, R.styleable.QuickPollProgressBar,
            defStyleAttr, 0
        )

        reachedBarColor = attributes.getColor(
            R.styleable.QuickPollProgressBar_progressReachedColor,
            Color.WHITE
        )
        textColor = attributes.getColor(
            R.styleable.QuickPollProgressBar_progressTextColor,
            Color.WHITE
        )
        textSize = attributes.getDimension(
            R.styleable.QuickPollProgressBar_progressTextSize,
            spToPx(14f, context)
        )
        reachedBarHeight = attributes.getDimension(
            R.styleable.QuickPollProgressBar_progressReachedBarHeight,
            dpToPx(12f, context)
        )
        offset = attributes.getDimension(
            R.styleable.QuickPollProgressBar_progressTextOffset,
            dpToPx(10f, context)
        )
        icon = attributes.getInt(
            R.styleable.QuickPollProgressBar_leftIcon,
            0
        )

        if (icon != 0) {
            iconBitmap = context.getBitmap(icon, iconBounds)
        }

        applyProgress(attributes.getInt(R.styleable.QuickPollProgressBar_currentProgress, 0))
        setMax(attributes.getInt(R.styleable.QuickPollProgressBar_progressMax, 100))

        attributes.recycle()

        initializePainters()
    }

    private fun startProgressAnimation() {
        val fromValue = if (initProgress > 0) {
            currentProgress / initProgress.toFloat() * 100
        } else {
            0
        }.toInt()

        progressAnimator = ValueAnimator.ofInt(fromValue, 100)
        progressAnimator.addUpdateListener {
            animationProgress = it.animatedValue as Int
            invalidate()
        }

        progressAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                progressAnimator.removeAllListeners()
            }
        })

        progressAnimator.start()
    }

    override fun getSuggestedMinimumWidth(): Int {
        return textSize.toInt() + (iconBitmap?.width ?: 0)
    }

    override fun getSuggestedMinimumHeight(): Int {
        return (iconBitmap?.height ?: textSize.coerceAtLeast(reachedBarHeight).toInt())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            measure(widthMeasureSpec, true),
            measure(heightMeasureSpec, false)
        )
    }

    private fun measure(measureSpec: Int, isWidth: Boolean): Int {
        var result: Int
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        val padding = if (isWidth) paddingLeft + paddingRight else paddingTop + paddingBottom
        if (mode == MeasureSpec.EXACTLY) {
            result = size
        } else {
            result = if (isWidth) suggestedMinimumWidth else suggestedMinimumHeight
            result += padding
            if (mode == MeasureSpec.AT_MOST) {
                result = if (isWidth) {
                    result.coerceAtLeast(size)
                } else {
                    result.coerceAtMost(size)
                }
            }
        }
        return result
    }

    private fun measureProgressRect() {
        this.currentProgress = initProgress
        currentDrawText = String.format("%d", currentProgress * 100 / maxProgress)
        currentDrawText = prefix + currentDrawText + suffix
        drawTextWidth = textPaint.measureText(currentDrawText)
        if (currentProgress == 0) {
            drawTextStart = paddingLeft.toFloat() + (iconBitmap?.width ?: 0f).toFloat() + offset
        } else {
            val right = (width - paddingLeft - paddingRight - offset - iconBounds.width()) /
                    maxProgress * currentProgress + paddingLeft + reachedBarHeight / 2

            reachedRectF.left = offset + paddingLeft.toFloat() + iconBounds.width()
            reachedRectF.top =
                (height + paddingTop - paddingBottom) / 2.0f - reachedBarHeight / 2.0f
            reachedRectF.right = reachedRectF.left + (right * animationProgress / 100)
            reachedRectF.bottom =
                (height + paddingTop - paddingBottom) / 2.0f + reachedBarHeight / 2.0f

            drawTextStart = reachedRectF.right + offset
        }
        drawTextEnd =
            (height + paddingTop - paddingBottom) / 2.0f - (textPaint.descent() + textPaint.ascent()) / 2.0f
        if (drawTextStart + drawTextWidth >= width - paddingRight) {
            drawTextStart = width - paddingRight - drawTextWidth
            reachedRectF.right = drawTextStart - offset
        }
    }

    private fun initializePainters() {
        reachedBarPaint.color = reachedBarColor
        textPaint.color = textColor
        textPaint.textSize = textSize
    }

    override fun onDraw(canvas: Canvas) {
        measureProgressRect()
        iconBitmap?.let {
            canvas.drawBitmap(
                it,
                paddingLeft.toFloat(),
                height / 2f - it.width / 2f,
                reachedBarPaint
            )
        }
        canvas.drawRoundRect(
            reachedRectF,
            reachedRectF.height(),
            reachedRectF.height(),
            reachedBarPaint
        )
        canvas.drawText(currentDrawText, drawTextStart, drawTextEnd, textPaint)
    }

    /**
     * Set the paint's text size. This value must be > 0
     *
     * @param textSize set the paint's text size in pixel units.
     */
    fun setProgressTextSize(textSize: Float) {
        this.textSize = textSize
        textPaint.textSize = this.textSize
    }

    /**
     * Set the text color.
     *
     * @param textColor The new color (including alpha) to set in the paint.
     */
    fun setProgressTextColor(textColor: Int) {
        this.textColor = textColor
        textPaint.color = this.textColor
    }

    /**
     * Set the progress bar color.
     *
     * @param progressColor The new color (including alpha) to set in the paint.
     */
    fun setReachedBarColor(progressColor: Int) {
        this.reachedBarColor = progressColor
        reachedBarPaint.color = reachedBarColor
    }

    /**
     * Set the upper range of the progress bar.
     *
     * @param maxProgress the upper range of this progress bar
     */
    fun setMax(maxProgress: Int) {
        if (maxProgress > 0) {
            this.maxProgress = maxProgress
        }
    }

    /**
     * Sets the current progress to the specified value. Does not do anything
     * if the progress is not in range 0..maxProgress.
     *
     * This method will immediately update the visual position of the progress
     * indicator. To animate the visual position to the target value, use [animate].
     *
     * @param progress the new progress, between 0 and [maxProgress]
     * @param animate true to animate between the current and target
     *                values or false to not animate
     */
    fun applyProgress(progress: Int, animate: Boolean = false) {
        if (progress in 0..maxProgress) {
            this.initProgress = progress
            if (animate) {
                startProgressAnimation()
            } else {
                invalidate()
            }
        }
    }

    /**
     * Set the left icon bitmap.
     *
     * @param iconBitmap set the left icon.
     */
    fun setIcon(iconBitmap: Bitmap) {
        this.iconBitmap = iconBitmap
    }

    /**
     * Set the prefix text to the progress bar view.
     *
     * @param prefix set the prefix text.
     */
    fun setPrefix(prefix: String) {
        this.prefix = prefix
    }

}