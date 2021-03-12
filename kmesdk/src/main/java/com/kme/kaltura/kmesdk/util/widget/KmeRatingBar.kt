package com.kme.kaltura.kmesdk.util.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.databinding.LayoutPollBtnRatingBinding
import com.kme.kaltura.kmesdk.getBitmapFromView
import kotlin.math.ceil
import kotlin.math.max


class KmeRatingBar @JvmOverloads constructor(
    context: Context, private val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val points: ArrayList<PointF> = arrayListOf()
    private val filledRateBitmaps: ArrayList<Bitmap> = arrayListOf()
    private val emptyRateBitmaps: ArrayList<Bitmap> = arrayListOf()

    private var isSliding = false
    private var slidePosition = 0f

    private var itemWidth = 0f

    private var listener: OnRatingSliderChangeListener? = null

    private var currentRating = NO_RATING
    private var numStars = MAX_RATE

    private var filledStarWidth = 0
    private var filledStarHeight = 0

    private var emptyStarWidth = 0
    private var emptyStarHeight = 0

    private var horizontalSpacing = 0

    private var isEnabled = false

    private var filledRateRes: Int = 0
    private var emptyRateRes: Int = 0

    var rating = NO_RATING
        private set

    private val layoutInflater by lazy { LayoutInflater.from(context) }

    private val defaultDrawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.ic_poll_empty_star)
    }

    init {
        init()
    }

    private fun init() {
        isSliding = false
        if (attrs != null) {
            val ta: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.KmeRatingBar, 0, 0)
            try {
                filledStarWidth =
                    ta.getDimensionPixelSize(R.styleable.KmeRatingBar_filledStarWidth, 0)
                filledStarHeight =
                    ta.getDimensionPixelSize(R.styleable.KmeRatingBar_filledStarHeight, 0)
                emptyStarWidth =
                    ta.getDimensionPixelSize(R.styleable.KmeRatingBar_emptyStarWidth, 0)
                emptyStarHeight =
                    ta.getDimensionPixelSize(R.styleable.KmeRatingBar_emptyStarHeight, 0)

                horizontalSpacing =
                    ta.getDimensionPixelSize(R.styleable.KmeRatingBar_starSpacing, 0)
                isEnabled = ta.getBoolean(R.styleable.KmeRatingBar_enabled, true)
                rating = ta.getInt(R.styleable.KmeRatingBar_rating, NO_RATING)
                numStars = ta.getInt(R.styleable.KmeRatingBar_numStars, MAX_RATE)

                filledRateRes = ta.getResourceId(
                    R.styleable.KmeRatingBar_drawableFilledRate,
                    R.drawable.ic_poll_empty_star
                )
                emptyRateRes = ta.getResourceId(
                    R.styleable.KmeRatingBar_drawableEmptyRate,
                    R.drawable.ic_poll_empty_star
                )

            } finally {
                ta.recycle()
            }
        }

        if (numStars <= 0) numStars = MAX_RATE

        for (i in 0 until numStars) {
            points.add(PointF())
        }

        if (rating != NO_RATING) setRating(rating)

        generateRateBitmaps()
    }

    private fun generateRateBitmaps() {
        val filledLayoutParams = ViewGroup.LayoutParams(filledStarWidth, filledStarHeight)
        val emptyLayoutParams = ViewGroup.LayoutParams(emptyStarWidth, emptyStarHeight)

        for (i in 0 until numStars) {
            val filledRateView = LayoutPollBtnRatingBinding.inflate(layoutInflater)
            val emptyRateView = LayoutPollBtnRatingBinding.inflate(layoutInflater)

            filledRateView.tvRating.text = i.inc().toString()
            emptyRateView.tvRating.text = i.inc().toString()

            val filledIconParams = filledRateView.ivIcon.layoutParams
            val emptyIconParams = emptyRateView.ivIcon.layoutParams
            filledIconParams.width = filledStarWidth
            filledIconParams.height = filledStarHeight
            emptyIconParams.width = emptyStarWidth
            emptyIconParams.height = emptyStarHeight

            filledRateView.ivIcon.setImageResource(filledRateRes)
            emptyRateView.ivIcon.setImageResource(emptyRateRes)

            filledRateView.ivIcon.layoutParams = filledIconParams
            emptyRateView.ivIcon.layoutParams = emptyIconParams

            filledRateView.root.apply {
                this.layoutParams = filledLayoutParams
                getBitmapFromView()?.let {
                    filledRateBitmaps.add(it)
                }
            }

            emptyRateView.root.apply {
                this.layoutParams = emptyLayoutParams
                getBitmapFromView()?.let {
                    emptyRateBitmaps.add(it)
                }
            }
        }

    }

    override fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        super.setEnabled(enabled)
    }

    override fun isEnabled(): Boolean {
        return isEnabled
    }

    /**
     * Set a listener that will be invoked whenever the users interacts with the KmeRatingBar.
     *
     * @param listener
     * Listener to set.
     */
    fun setOnRatingSliderChangeListener(listener: OnRatingSliderChangeListener?) {
        this.listener = listener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled()) {
            // Disable all input if the slider is disabled
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                isSliding = true
                slidePosition = getRelativePosition(event.x)
                rating = ceil(slidePosition.toDouble()).toInt()
                listener?.let {
                    if (rating != currentRating) {
                        currentRating = rating
                        it.onPendingRating(rating)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                currentRating = NO_RATING
                listener?.onFinalRating(ceil(slidePosition.toDouble()).toInt())
                rating = ceil(slidePosition.toDouble()).toInt()
            }
            MotionEvent.ACTION_CANCEL -> {
                currentRating = NO_RATING
                listener?.onCancelRating()
                isSliding = false
            }
            else -> {
            }
        }
        invalidate()
        return true
    }

    private fun getRelativePosition(x: Float): Float {
        var position = x / itemWidth
        position = position.coerceAtLeast(0f)
        return position.coerceAtMost(numStars.toFloat())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        itemWidth = w / numStars.toFloat()
        updatePositions()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width: Int =
            max(filledStarWidth, emptyStarWidth) * numStars + horizontalSpacing * (numStars - 1) +
                    paddingLeft + paddingRight
        val height: Int = max(filledStarHeight, emptyStarHeight) + paddingTop + paddingBottom
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until numStars) {
            val point = points[i]
            canvas.save()
            canvas.translate(point.x, point.y)
            drawRateItem(canvas, i)
            canvas.restore()
        }
    }

    private fun drawRateItem(canvas: Canvas, position: Int) {
        if (isSliding && position <= slidePosition) {
            val rating = ceil(slidePosition.toDouble()).toInt()
            if (rating > 0)
                drawRateItem(canvas, filledRateBitmaps[position])
            else
                drawRateItem(canvas, emptyRateBitmaps[position])
        } else {
            drawRateItem(canvas, emptyRateBitmaps[position])
        }
    }

    private fun drawRateItem(canvas: Canvas, bitmap: Bitmap?) {
        if (bitmap == null) return

        canvas.save()
        canvas.translate((-bitmap.width / 2).toFloat(), (-bitmap.height / 2).toFloat())
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        canvas.restore()
    }

    private fun updatePositions() {
        var left = 0f
        for (i in 0 until numStars) {
            val posY: Float = (height / 2).toFloat()
            var posX = left + max(filledStarWidth, emptyStarWidth) / 2
            left += max(filledStarWidth, emptyStarWidth).toFloat()
            if (i > 0) {
                posX += horizontalSpacing.toFloat()
                left += horizontalSpacing.toFloat()
            } else {
                posX += paddingLeft.toFloat()
                left += paddingLeft.toFloat()
            }
            points[i].set(posX, posY)
        }
    }

    fun setRating(rating: Int) {
        if (rating < 0) throw IndexOutOfBoundsException("Rating must be more than 0")
        this.rating = rating
        slidePosition = (rating - 0.1).toFloat()
        isSliding = true
        invalidate()
        listener?.onFinalRating(rating)
    }

    /**
     * A callback that notifies clients when the user starts rating, changes the rating
     * value and when the rating has ended.
     */
    interface OnRatingSliderChangeListener {
        /**
         * Notification that the user has moved over to a different rating value.
         * The rating value is only temporary and might change again before the
         * rating is finalized.
         *
         * @param rating
         * the pending rating. A value between 0 and 5.
         */
        fun onPendingRating(rating: Int)

        /**
         * Notification that the user has selected a final rating.
         *
         * @param rating
         * the final rating selected. A value between 0 and 5.
         */
        fun onFinalRating(rating: Int)

        /**
         * Notification that the user has canceled the rating.
         */
        fun onCancelRating()
    }

    companion object {
        private const val NO_RATING = 0
        private const val MAX_RATE = 5
    }

}