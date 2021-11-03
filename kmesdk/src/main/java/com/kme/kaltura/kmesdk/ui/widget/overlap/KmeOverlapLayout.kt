package com.kme.kaltura.kmesdk.ui.widget.overlap

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginRight
import com.kme.kaltura.kmesdk.databinding.LayoutOverlapViewBinding
import com.kme.kaltura.kmesdk.getDisplayMetrics
import com.kme.kaltura.kmesdk.isLandscape
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt


class KmeOverlapLayout @JvmOverloads constructor(
    context: Context,
    val attrs: AttributeSet? = null,
    val defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IFloatingContainer {

    var listener: OnOverlapListener? = null

    var layoutRatio = (1280 / 720f)

    var state: State = State.NONE
        private set

    var verticalOffset = 0
        set(value) {
            field = value
            preMeasure()
            postDragging()
        }
    var horizontalOffset = 0
        set(value) {
            field = value
            preMeasure()
            postDragging()
        }

    private var isLandscape: Boolean = false

    private var isNewConfig: Boolean = false

    private var savedLayoutStates: MutableList<KmeOverlapSavedState?>? = null

    private val resizableLayoutRect by lazy { Rect() }

    private val gestureDetector by lazy { OverlapGestureDetector(context) }

    private val displayMetrics by lazy { context.getDisplayMetrics() }

    private val cornerPoint1 by lazy { Point() }
    private val cornerPoint2 by lazy { Point() }
    private val cornerPoint3 by lazy { Point() }
    private val cornerPoint4 by lazy { Point() }

    private var binding = LayoutOverlapViewBinding.inflate(
        LayoutInflater.from(context),
        this
    )
    
    private var scaleFactor = 1f
    private var maxZoom = 2f

    private var deltaX: Float = 0f
    private var deltaY: Float = 0f

    private var minResizeWidth: Int = 0
    private var minResizeHeight: Int = 0

    private var preWidth: Int = 0
    private var preHeight: Int = 0

    private var saveX = 0f
    private var saveY = 0f
    private var saveWidth = 0
    private var saveHeight = 0


    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            performResizing()
            return true
        }
    }

    private val scaleDetector = ScaleGestureDetector(context, scaleListener)

    init {
        isLandscape = context.isLandscape()
        initResizableLayout()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        isLandscape = context.isLandscape()
        savedLayoutStates = if (state is Bundle) {
            state.getParcelableArrayList<KmeOverlapSavedState?>(SAVED_STATE_EXTRA)?.toMutableList()
        } else {
            null
        }
        super.onRestoreInstanceState(BaseSavedState.EMPTY_STATE)

        restoreView()
    }

    private fun initResizableLayout() {
        binding.resizableLayout.doOnLayout {
            saveX = it.x
            saveY = it.y
            saveWidth = it.width
            saveHeight = it.height

            preMeasure()
            postDragging()
        }

        val rootWidth = displayMetrics.widthPixels
        val rootHeight = displayMetrics.heightPixels

        minResizeWidth = if (isLandscape) {
            rootHeight / 2f
        } else {
            rootWidth / 2f
        }.roundToInt()

        minResizeHeight = (minResizeWidth / layoutRatio).toInt()

        binding.resizableLayout.layoutParams =
            LayoutParams(minResizeWidth, minResizeHeight, Gravity.BOTTOM or Gravity.END)
    }

    private fun restoreView() {
        val savedState = savedLayoutStates?.find { it?.isLandscape == isLandscape }

        savedState?.let {
            if (it.width > 0 || it.height > 0) {
                saveX = it.posX
                saveY = it.posY
                saveWidth = it.width
                saveHeight = it.height

                binding.resizableLayout.layoutParams = LayoutParams(saveWidth, saveHeight)
                binding.resizableLayout.x = saveX
                binding.resizableLayout.y = saveY
            }
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
//        scaleDetector.onTouchEvent(event)
        gestureDetector.handleTouchEvents(event)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                preMeasure()
                measureDelta(event)
            }
            MotionEvent.ACTION_MOVE -> {
                if (state == State.DRAGGING) {
                    performDragging(event)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (state == State.DRAGGING) {
                    postDragging()
                }

                deltaX = 0f
                deltaY = 0f
                state = State.NONE
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    private fun preMeasure() {
        preWidth = binding.resizableLayout.width
        preHeight = binding.resizableLayout.height

        val left = binding.root.paddingLeft + horizontalOffset
        val top = binding.root.paddingTop + verticalOffset
        val right =
            binding.root.right - binding.root.paddingRight - binding.root.marginRight - horizontalOffset
        val bottom =
            binding.root.bottom - binding.root.paddingBottom - binding.root.marginBottom - verticalOffset

        cornerPoint1.set(left, top)
        cornerPoint2.set(right, top)
        cornerPoint3.set(left, bottom)
        cornerPoint4.set(right, bottom)
    }

    private fun measureDelta(event: MotionEvent) {
        binding.resizableLayout.getGlobalVisibleRect(resizableLayoutRect)

        if (resizableLayoutRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
            deltaX = binding.resizableLayout.x - event.rawX
            deltaY = binding.resizableLayout.y - event.rawY
            state = State.DRAGGING
        }
    }

    private fun performDragging(event: MotionEvent) {
        var newX = event.rawX + deltaX
        var newY = event.rawY + deltaY

        if (newX in binding.resizableLayout.x..binding.resizableLayout.x + THRESHOLD) {
            newX = binding.resizableLayout.x
        }

        if (newY in binding.resizableLayout.y..binding.resizableLayout.y + THRESHOLD) {
            newY = binding.resizableLayout.y
        }

        binding.resizableLayout.animate()
            .x(newX)
            .y(newY)
            .setDuration(0)
            .start()
    }

    private fun postDragging() {
        val layout = binding.resizableLayout

        val point = Point(
            layout.x.toInt() + layout.width / 2,
            layout.y.toInt() + layout.height / 2
        )

        val closestPoint = minOf(point, cornerPoint1, cornerPoint2, cornerPoint3, cornerPoint4)

        var toX = closestPoint.x.toFloat()
        var toY = closestPoint.y.toFloat()

        if (toX >= cornerPoint2.x) {
            toX -= layout.width
        }

        if (toY >= cornerPoint3.y) {
            toY -= layout.height
        }

        saveX = toX
        saveY = toY

        binding.resizableLayout.animate()
            .x(toX)
            .y(toY)
            .setDuration(300)
            .start()
    }

    private fun performResizing() {
        val layoutParams = binding.resizableLayout.layoutParams

        var newWidth = (preWidth * scaleFactor).roundToInt()
        var newHeight = (preHeight * scaleFactor).roundToInt()

        val maxWidth = binding.root.width
        val maxHeight = binding.root.height

        if (newWidth < minResizeWidth) {
            newWidth = minResizeWidth
        } else if (newWidth > maxWidth) {
            newWidth = maxWidth
        }

        if (newHeight < minResizeHeight) {
            newHeight = minResizeHeight
        } else if (newHeight > maxHeight) {
            newHeight = maxHeight
        }

        if (newWidth !in preWidth..preWidth + THRESHOLD) {
            layoutParams.width = newWidth
        }
        if (newHeight !in preHeight..preHeight + THRESHOLD) {
            layoutParams.height = newHeight
        }

        binding.resizableLayout.layoutParams = layoutParams

        saveWidth = layoutParams.width
        saveHeight = layoutParams.height
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        preMeasure()
        postDragging()
    }

    override fun onFloatingRectChanged(rect: Rect) {
//        with(binding.root) {
//            if (left != rect.left || top != rect.top || right != rect.right || bottom != rect.bottom) {
//                apply {
//                    left = rect.left
//                    top = rect.top
//                    right = rect.right
//                    bottom = rect.bottom
//                }
//
//                preMeasure()
//                postDragging()
//            }
//        }
    }

    override fun onSaveInstanceState(): Parcelable {
        super.onSaveInstanceState()
        if (isNewConfig) {
            isLandscape = !isLandscape
        }

        isNewConfig = false

        return Bundle().apply {
            val state = KmeOverlapSavedState(
                saveX,
                saveY,
                saveWidth,
                saveHeight,
                isLandscape
            )

            savedLayoutStates?.let {
                it.removeAll { item -> item?.isLandscape == isLandscape }
                it.add(state)
            } ?: run {
                savedLayoutStates = mutableListOf(state)
            }


            putParcelableArrayList(SAVED_STATE_EXTRA, ArrayList(savedLayoutStates))
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        isLandscape = newConfig?.orientation == Configuration.ORIENTATION_LANDSCAPE
        isNewConfig = true
    }

    private inner class OverlapGestureDetector(context: Context) :
        GestureDetector.SimpleOnGestureListener() {

        private val gestureDetector = GestureDetector(context, this)

        fun handleTouchEvents(event: MotionEvent?): Boolean = gestureDetector.onTouchEvent(event)

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            listener?.onSwitchContent()
            return true
        }
    }

    fun getContentFrameId(): Int {
        return binding.contentLayout.id
    }

    private fun minOf(a: Point, vararg other: Point): Point {
        var minDistance = Int.MAX_VALUE
        var closestPoint = a

        other.forEach { point ->
            val distance = a.distance(point)

            if (distance < minDistance) {
                minDistance = distance
                closestPoint = point
            }
        }

        return closestPoint
    }

    private fun Point.distance(point: Point): Int {
        return sqrt(
            (point.y - this.y).toFloat().pow(2)
                    + (point.x - this.x).toFloat().pow(2)
        ).toInt()
    }

    enum class State {
        DRAGGING, NONE
    }

    interface OnOverlapListener {
        fun onSwitchContent()
    }

    companion object {
        private const val THRESHOLD = 5
        private const val SAVED_STATE_EXTRA = "SAVED_STATE_EXTRA"
    }

}