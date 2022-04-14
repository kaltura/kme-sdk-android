package com.kme.kaltura.kmesdk.ui.widget.zoom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.OverScroller
import androidx.core.view.ViewCompat
import com.kme.kaltura.kmesdk.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ZoomLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private var mDoubleClickZoom = 0f
    private var mMinZoom = 0f
    private var mMaxZoom = 0f
    private var mCurrentZoom = 1f
    private var mMinimumVelocity = 0
    private var mMaximumVelocity = 0
    private var mScrollBegin = false

    private var mScaleDetector: ScaleGestureDetector? = null
    private var mGestureDetector: GestureDetector? = null
    private var mOverScroller: OverScroller? = null
    private var mScaleHelper: ScaleHelper? = null
    private var mAccelerateInterpolator: AccelerateInterpolator? = null
    private var mDecelerateInterpolator: DecelerateInterpolator? = null
    private var mZoomLayoutGestureListener: ZoomLayoutGestureListener? = null
    private var mLastChildHeight = 0
    private var mLastChildWidth = 0
    private var mLastHeight = 0
    private var mLastWidth = 0
    private var mLastCenterX = 0
    private var mLastCenterY = 0
    private var mNeedReScale = false

    private val mSimpleOnScaleGestureListener = object : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            if (!isEnabled) {
                return false
            }
            var newScale: Float
            newScale = mCurrentZoom * detector.scaleFactor
            if (newScale > mMaxZoom) {
                newScale = mMaxZoom
            } else if (newScale < mMinZoom) {
                newScale = mMinZoom
            }
            setScale(newScale, detector.focusX.toInt(), detector.focusY.toInt())
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mZoomLayoutGestureListener?.onScaleGestureBegin()
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {}
    }

    private val mSimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            if (mOverScroller?.isFinished == false) {
                mOverScroller?.abortAnimation()
            }
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            val newScale: Float = when {
                mCurrentZoom < 1 -> {
                    1f
                }
                mCurrentZoom < mDoubleClickZoom -> {
                    mDoubleClickZoom
                }
                else -> {
                    1f
                }
            }
            smoothScale(newScale, e.x.toInt(), e.y.toInt())
            mZoomLayoutGestureListener?.onDoubleTap()
            return true
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (!isEnabled) {
                return false
            }
            if (!mScrollBegin) {
                mScrollBegin = true
                mZoomLayoutGestureListener?.onScrollBegin()
            }
            processScroll(
                distanceX.toInt(),
                distanceY.toInt(),
                getScrollRangeX(),
                getScrollRangeY()
            )
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (!isEnabled) {
                return false
            }
            fling((-velocityX).toInt(), (-velocityY).toInt())
            return true
        }
    }


    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        mScaleDetector = ScaleGestureDetector(context, mSimpleOnScaleGestureListener)
        mGestureDetector = GestureDetector(context, mSimpleOnGestureListener)
        mOverScroller = OverScroller(getContext())
        mScaleHelper = ScaleHelper()
        val configuration = ViewConfiguration.get(getContext())
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity
        setWillNotDraw(false)
        if (attrs != null) {
            var array: TypedArray? = null
            try {
                array = context.obtainStyledAttributes(attrs, R.styleable.ZoomLayout)
                mMinZoom =
                    array.getFloat(R.styleable.ZoomLayout_min_zoom, DEFAULT_MIN_ZOOM)
                mMaxZoom =
                    array.getFloat(R.styleable.ZoomLayout_max_zoom, DEFAULT_MAX_ZOOM)
                mDoubleClickZoom = array.getFloat(
                    R.styleable.ZoomLayout_double_click_zoom,
                    DEFAULT_DOUBLE_CLICK_ZOOM
                )
                if (mDoubleClickZoom > mMaxZoom) {
                    mDoubleClickZoom = mMaxZoom
                }
            } catch (e: Exception) {
                Log.e(TAG, TAG, e)
            } finally {
                array?.recycle()
            }
        }
    }

    private fun fling(velX: Int, velY: Int): Boolean {
        var velocityX = velX
        var velocityY = velY
        if (abs(velocityX) < mMinimumVelocity) {
            velocityX = 0
        }
        if (abs(velocityY) < mMinimumVelocity) {
            velocityY = 0
        }
        val scrollY = scrollY
        val scrollX = scrollX
        val canFlingX = scrollX > 0 && scrollX < getScrollRangeX()
        val canFlingY = scrollY > 0 && scrollY < getScrollRangeY()
        val canFling = canFlingY || canFlingX
        if (canFling) {
            velocityX = max(-mMaximumVelocity, min(velocityX, mMaximumVelocity))
            velocityY = max(-mMaximumVelocity, min(velocityY, mMaximumVelocity))
            val height = height - paddingBottom - paddingTop
            val width = width - paddingRight - paddingLeft
            val bottom: Int = getContentHeight()
            val right: Int = getContentWidth()
            mOverScroller?.fling(
                getScrollX(), getScrollY(),
                velocityX, velocityY,
                0, max(0, right - width),
                0, max(0, bottom - height),
                0, 0
            )
            notifyInvalidate()
            return true
        }
        return false
    }

    fun smoothScale(newScale: Float, centerX: Int, centerY: Int) {
        if (mCurrentZoom > newScale) {
            if (mAccelerateInterpolator == null) {
                mAccelerateInterpolator = AccelerateInterpolator()
            }
            mScaleHelper?.startScale(
                mCurrentZoom,
                newScale,
                centerX,
                centerY,
                mAccelerateInterpolator
            )
        } else {
            if (mDecelerateInterpolator == null) {
                mDecelerateInterpolator = DecelerateInterpolator()
            }
            mScaleHelper?.startScale(
                mCurrentZoom,
                newScale,
                centerX,
                centerY,
                mDecelerateInterpolator
            )
        }
        notifyInvalidate()
    }

    private fun notifyInvalidate() {
        ViewCompat.postInvalidateOnAnimation(this)
    }

    fun setScale(scale: Float, centerX: Int, centerY: Int) {
        mLastCenterX = centerX
        mLastCenterY = centerY
        val preScale = mCurrentZoom
        mCurrentZoom = scale
        val sX = scrollX
        val sY = scrollY
        val dx = ((sX + centerX) * (scale / preScale - 1)).toInt()
        val dy = ((sY + centerY) * (scale / preScale - 1)).toInt()
        if (getScrollRangeX() < 0) {
            child().pivotX = (child().width / 2).toFloat()
            child().translationX = 0f
        } else {
            child().pivotX = 0f
            val willTranslateX: Int = -child().left
            child().translationX = willTranslateX.toFloat()
        }
        if (getScrollRangeY() < 0) {
            child().pivotY = (child().height / 2).toFloat()
            child().translationY = 0f
        } else {
            val willTranslateY: Int = -child().top
            child().translationY = willTranslateY.toFloat()
            child().pivotY = 0f
        }
        child().scaleX = mCurrentZoom
        child().scaleY = mCurrentZoom
        processScroll(dx, dy, getScrollRangeX(), getScrollRangeY())
        notifyInvalidate()
    }

    private fun processScroll(
        deltaX: Int, deltaY: Int,
        scrollRangeX: Int, scrollRangeY: Int
    ) {
        val oldScrollX = scrollX
        val oldScrollY = scrollY
        var newScrollX = oldScrollX + deltaX
        var newScrollY = oldScrollY + deltaY
        val left = 0
        val top = 0

        if (newScrollX > scrollRangeX) {
            newScrollX = scrollRangeX
        } else if (newScrollX < left) {
            newScrollX = left
        }

        if (newScrollY > scrollRangeY) {
            newScrollY = scrollRangeY
        } else if (newScrollY < top) {
            newScrollY = top
        }

        if (newScrollX < 0) {
            newScrollX = 0
        }

        if (newScrollY < 0) {
            newScrollY = 0
        }

        scrollTo(newScrollX, newScrollY)
    }

    private fun getScrollRangeX(): Int {
        val contentWidth = width - paddingRight - paddingLeft
        return getContentWidth() - contentWidth
    }

    private fun getContentWidth(): Int {
        return (child().width * mCurrentZoom).toInt()
    }

    private fun getScrollRangeY(): Int {
        val contentHeight = height - paddingBottom - paddingTop
        return getContentHeight() - contentHeight
    }

    private fun getContentHeight(): Int {
        return (child().height * mCurrentZoom).toInt()
    }

    private fun child(): View {
        return getChildAt(0)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (mNeedReScale) {
            setScale(mCurrentZoom, mLastCenterX, mLastCenterY)
            mNeedReScale = false
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        child().isClickable = true
        gravity = if (child().height < height || child().width < width) {
            Gravity.CENTER
        } else {
            Gravity.TOP
        }
        if (mLastChildWidth != child().width ||
            mLastChildHeight != child().height ||
            mLastWidth != width || mLastHeight != height
        ) {
            mNeedReScale = true
        }
        mLastChildWidth = child().width
        mLastChildHeight = child().height
        mLastWidth = child().width
        mLastHeight = height

        if (mNeedReScale) {
            notifyInvalidate()
        }
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScaleHelper?.computeScrollOffset() == true) {
            setScale(
                mScaleHelper?.curScale ?: 1f,
                mScaleHelper?.startX ?: 0,
                mScaleHelper?.startY ?: 0
            )
        }
        if (mOverScroller?.computeScrollOffset() == true) {
            val oldX = scrollX
            val oldY = scrollY
            val x = mOverScroller!!.currX
            val y = mOverScroller!!.currY
            if (oldX != x || oldY != y) {
                val rangeY = getScrollRangeY()
                val rangeX = getScrollRangeX()
                processScroll(x - oldX, y - oldY, rangeX, rangeY)
            }
            if (mOverScroller?.isFinished == false) {
                notifyInvalidate()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {
            mScrollBegin = false
        }
        mGestureDetector?.onTouchEvent(ev)
        mScaleDetector?.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun measureChildWithMargins(
        child: View, parentWidthMeasureSpec: Int, widthUsed: Int,
        parentHeightMeasureSpec: Int, heightUsed: Int
    ) {
        val lp = child.layoutParams as MarginLayoutParams
        val childWidthMeasureSpec = getChildMeasureSpec(
            parentWidthMeasureSpec,
            paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin
                    + widthUsed, lp.width
        )
        val usedTotal = (paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin +
                heightUsed)
        val childHeightMeasureSpec = if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            MeasureSpec.makeMeasureSpec(
                max(0, MeasureSpec.getSize(parentHeightMeasureSpec) - usedTotal),
                MeasureSpec.UNSPECIFIED
            )
        } else {
            getChildMeasureSpec(
                parentHeightMeasureSpec,
                (paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin
                        + heightUsed), lp.height
            )
        }
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return if (direction > 0) {
            scrollX < getScrollRangeX()
        } else {
            scrollX > 0 && getScrollRangeX() > 0
        }
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return if (direction > 0) {
            scrollY < getScrollRangeY()
        } else {
            scrollY > 0 && getScrollRangeY() > 0
        }
    }

    fun setZoomLayoutGestureListener(zoomLayoutGestureListener: ZoomLayoutGestureListener) {
        mZoomLayoutGestureListener = zoomLayoutGestureListener
    }

    interface ZoomLayoutGestureListener {
        fun onScrollBegin()
        fun onScaleGestureBegin()
        fun onDoubleTap()
    }

    companion object {
        private const val TAG = "ZoomLayout"
        private const val DEFAULT_MIN_ZOOM = 1.0f
        private const val DEFAULT_MAX_ZOOM = 4.0f
        private const val DEFAULT_DOUBLE_CLICK_ZOOM = 2.0f
    }

}