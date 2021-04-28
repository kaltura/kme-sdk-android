package com.kme.kaltura.kmesdk.ui.widget.overlap

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import com.kme.kaltura.kmesdk.databinding.LayoutOverlapViewBinding
import com.kme.kaltura.kmesdk.getDisplayMetrics
import com.kme.kaltura.kmesdk.gone
import com.kme.kaltura.kmesdk.isLandscape

class KmeOverlapLayout @JvmOverloads constructor(
    context: Context,
    val attrs: AttributeSet? = null,
    val defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var listener: OnOverlapListener? = null
    var state: State = State.NONE
        private set
    private var isLandscape: Boolean = false

    private var isNewConfig: Boolean = false

    private var savedLayoutStates: MutableList<KmeOverlapSavedState?>? = null

    private val resizableLayoutRect by lazy { Rect() }

    private val resizeButtonRect by lazy { Rect() }
    private val gestureDetector by lazy { OverlapGestureDetector(context) }

    private var binding = LayoutOverlapViewBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    private var isMovingMode = false

    private var deltaX: Float = 0f
    private var deltaY: Float = 0f

    private var minResizeWidth: Int = 0
    private var minResizeHeight: Int = 0

    var defaultDownScale = 3

    init {
        isLandscape = context.isLandscape()
        initResizableLayout()
        initCloseButton()
        initSwitchContentButton()
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
        val displayMetrics = context.getDisplayMetrics()
        minResizeWidth = if (isLandscape) {
            displayMetrics.widthPixels / defaultDownScale
        } else {
            displayMetrics.heightPixels / defaultDownScale
        }

        minResizeHeight = if (isLandscape) {
            displayMetrics.heightPixels / defaultDownScale
        } else {
            displayMetrics.widthPixels / defaultDownScale
        }

        binding.resizableLayout.layoutParams = LayoutParams(minResizeWidth, minResizeHeight)
    }

    private fun restoreView() {
        val savedState = savedLayoutStates?.find { it?.isLandscape == isLandscape }

        savedState?.let {
            if (it.width > 0 || it.height > 0) {
                binding.resizableLayout.layoutParams = LayoutParams(it.width, it.height)
                binding.resizableLayout.x = it.posX
                binding.resizableLayout.y = it.posY
            }
        }
    }

    private fun initCloseButton() {
        if (isLandscape) {
            binding.ivCloseOverlap.gone()
        } else {
            binding.ivCloseOverlap.setOnClickListener {
                listener?.onOverlapClose()
            }
        }
    }

    private fun initSwitchContentButton() {
        binding.ivSwitchContent.setOnClickListener {
            listener?.onSwitchContent()
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.handleTouchEvents(event)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                measureDelta(event)
            }
            MotionEvent.ACTION_MOVE -> {
                if (isMovingMode && state == State.DRAGGING) {
                    performDragging(event)
                } else if (state == State.RESIZING) {
                    performResizing(event)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                deltaX = 0f
                deltaY = 0f
                isMovingMode = false
                state = State.NONE
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    private fun measureDelta(event: MotionEvent) {
        binding.resizableLayout.getGlobalVisibleRect(resizableLayoutRect)
        binding.ivResize.getGlobalVisibleRect(resizeButtonRect)
        if (resizeButtonRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
            deltaX = binding.ivResize.x - event.rawX
            deltaY = binding.ivResize.y - event.rawY
            state = State.RESIZING
        } else if (resizableLayoutRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
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

    private fun performResizing(event: MotionEvent) {
        val layoutParams = binding.resizableLayout.layoutParams

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
        binding.resizableLayout.layoutParams = layoutParams
    }

    override fun onSaveInstanceState(): Parcelable {
        super.onSaveInstanceState()
        if (isNewConfig) {
            isLandscape = !isLandscape
        }

        isNewConfig = false

        return Bundle().apply {
            val state = KmeOverlapSavedState(
                binding.resizableLayout.x,
                binding.resizableLayout.y,
                binding.resizableLayout.width,
                binding.resizableLayout.height,
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

        override fun onLongPress(event: MotionEvent?) {
            super.onLongPress(event)
            isMovingMode = true
        }
    }

    fun getContentFrameId(): Int {
        return binding.contentLayout.id
    }

    enum class State {
        DRAGGING, RESIZING, NONE
    }

    interface OnOverlapListener {
        fun onOverlapClose()

        fun onSwitchContent()
    }

    companion object {
        private const val THRESHOLD = 5
        private const val SAVED_STATE_EXTRA = "SAVED_STATE_EXTRA"
    }

}