package com.kme.kaltura.kmesdk.util.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.getDisplayMetrics

class KmeOverlapLayout @JvmOverloads constructor(
    context: Context,
    val attrs: AttributeSet? = null,
    val defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var listener: OnOverlapListener? = null

    var state: State = State.NONE
        private set

    lateinit var contentLayout: FrameLayout
        private set

    private lateinit var resizableLayout: FrameLayout
    private lateinit var resizeButton: View

    private val resizableLayoutRect by lazy { Rect() }
    private val resizeButtonRect by lazy { Rect() }
    private val gestureDetector by lazy { OverlapGestureDetector(context) }

    private var isMovingMode = false

    private var deltaX: Float = 0f
    private var deltaY: Float = 0f

    private var minResizeWidth: Int = 0
    private var minResizeHeight: Int = 0

    var defaultDownScale = 3

    init {
        addResizableLayout()
    }

    private fun addResizableLayout() {
        resizableLayout = initResizableLayout().apply {
            contentLayout = initContentLayout()
            resizeButton = initResizeButton()

            addView(contentLayout)
            addView(resizeButton)
            addView(initCloseButton())
        }

        addView(resizableLayout)
    }

    private fun initResizableLayout(): FrameLayout {
        return FrameLayout(context).apply {
            val displayMetrics = context.getDisplayMetrics()
            id = ::resizableLayout.name.hashCode()
            minResizeWidth = displayMetrics.heightPixels / defaultDownScale
            minResizeHeight = displayMetrics.widthPixels / defaultDownScale
            layoutParams = LayoutParams(minResizeWidth, minResizeHeight)
        }
    }

    private fun initCloseButton(): AppCompatImageView {
        return AppCompatImageView(context).apply {
            setImageResource(R.drawable.ic_open_in_new_off)
            isClickable = true
            isFocusable = true
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.TOP.or(Gravity.END)
            ).apply {
                val margins = resources.getDimensionPixelSize(R.dimen.default_margin_small)
                setMargins(margins, margins, margins, margins)
            }
            setOnClickListener {
                listener?.onOverlapClose()
            }
        }
    }

    private fun initResizeButton(): AppCompatImageView {
        return AppCompatImageView(context).apply {
            setImageResource(R.drawable.ic_resize)
            isClickable = true
            isFocusable = true
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM.or(Gravity.END)
            ).apply {
                val margin = resources.getDimensionPixelSize(R.dimen.default_margin_small)
                setMargins(margin, margin, margin, margin)
            }
        }
    }

    private fun initContentLayout(): FrameLayout {
        return FrameLayout(context).apply {
            id = ::contentLayout.name.hashCode()
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.handleTouchEvents(event)
        if (::resizableLayout.isInitialized) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    measureDelta(event)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isMovingMode) {
                        when (state) {
                            State.RESIZING -> {
                                performResizing(event)
                            }
                            State.DRAGGING -> {
                                performDragging(event)
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    deltaX = 0f
                    deltaY = 0f
                    isMovingMode = false
                    state = State.NONE
                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    private fun measureDelta(event: MotionEvent) {
        resizableLayout.getGlobalVisibleRect(resizableLayoutRect)
        resizeButton.getGlobalVisibleRect(resizeButtonRect)
        if (resizeButtonRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
            deltaX = resizeButton.x - event.rawX
            deltaY = resizeButton.y - event.rawY
            state = State.RESIZING
        } else if (resizableLayoutRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
            deltaX = resizableLayout.x - event.rawX
            deltaY = resizableLayout.y - event.rawY
            state = State.DRAGGING
        }
    }

    private fun performDragging(event: MotionEvent) {
        var newX = event.rawX + deltaX
        var newY = event.rawY + deltaY

        if (newX in resizableLayout.x..resizableLayout.x + THRESHOLD) {
            newX = resizableLayout.x
        }

        if (newY in resizableLayout.y..resizableLayout.y + THRESHOLD) {
            newY = resizableLayout.y
        }

        resizableLayout.animate()
            .x(newX)
            .y(newY)
            .setDuration(0)
            .start()
    }

    private fun performResizing(event: MotionEvent) {
        val layoutParams = resizableLayout.layoutParams

        var newWidth = (event.rawX + deltaX).toInt()
        var newHeight = (event.rawY + deltaY).toInt()

        if (newWidth < minResizeWidth) {
            newWidth = minResizeWidth
        }

        if (newHeight < minResizeHeight) {
            newHeight = minResizeHeight
        }

        if (newWidth !in layoutParams.width..layoutParams.width + THRESHOLD) {
            layoutParams.width = newWidth
        }
        if (newHeight !in layoutParams.height..layoutParams.height + THRESHOLD) {
            layoutParams.height = newHeight
        }
        resizableLayout.layoutParams = layoutParams
    }

    private inner class OverlapGestureDetector(context: Context) :
        GestureDetector.SimpleOnGestureListener() {

        private val gestureDetector = GestureDetector(context, this)


        fun handleTouchEvents(event: MotionEvent?): Boolean =
            gestureDetector.onTouchEvent(event)

        override fun onLongPress(event: MotionEvent?) {
            super.onLongPress(event)
            isMovingMode = true
        }
    }

    enum class State {
        DRAGGING, RESIZING, NONE
    }

    interface OnOverlapListener {
        fun onOverlapClose()
    }

    companion object {
        private const val THRESHOLD = 5
    }

}