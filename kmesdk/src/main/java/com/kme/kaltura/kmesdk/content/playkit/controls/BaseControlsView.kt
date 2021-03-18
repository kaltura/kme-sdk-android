package com.kme.kaltura.kmesdk.content.playkit.controls

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class BaseControlsView : FrameLayout, CoroutineScope {

    var seekBarChangeListener: OnSeekBarChangeListener? = null
    var controlsEventListener: OnControlsEventListener? = null

    var autoHideTimeout = DEFAULT_AUTO_HIDE_TIMEOUT

    private var hideControlsJob: Job? = null

    protected var isPlaying: Boolean = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setupAutoHideControls()
        setOnClickListener {
            if (isControlsVisible()) {
                setControlsVisibility(false)
            } else {
                setControlsVisibility(true)
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    fun formatTime(totalSeconds: Long): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0)
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        else
            String.format("%02d:%02d", minutes, seconds)
    }

    fun progressBarValue(position: Long, duration: Long): Int {
        return if (duration > 0) {
            (position * PROGRESS_BAR_MAX / duration).toInt()
        } else {
            0
        }
    }

    private fun setupAutoHideControls() {
        if (isPlaying) {
            hideControlsJob = launch {
                delay(autoHideTimeout)
                setControlsVisibility(false)
            }
        }
    }

    protected fun resetAutoHideControls() {
        cancelAutoHideControls()
        setupAutoHideControls()
    }

    private fun cancelAutoHideControls() {
        hideControlsJob?.cancel()
        hideControlsJob = null
    }

    override fun onDetachedFromWindow() {
        cancelAutoHideControls()
        super.onDetachedFromWindow()
    }

    open fun setControlsVisibility(doShow: Boolean) {
        if (doShow) {
            resetAutoHideControls()
        } else {
            cancelAutoHideControls()
        }
    }

    abstract fun isControlsVisible(): Boolean
    abstract fun setControlsMode(isEnabled: Boolean)
    abstract fun setProgressBarVisibility(toShow: Boolean)
    abstract fun setSeekBarVisibility(toShow: Boolean)
    abstract fun setSeekBarProgress(progress: Int)
    abstract fun setSeekBarMode(isEnabled: Boolean)
    abstract fun setSeekBarSecondaryProgress(secondaryProgress: Int)
    abstract fun setPlayPauseVisibility(toShow: Boolean)
    abstract fun setTimeVisibility(toShow: Boolean)
    abstract fun setTimePosition(timeSeconds: Long, durationSeconds: Long)
    abstract fun updateUI(event: PlayerControlsEvent)

    interface OnSeekBarChangeListener {
        fun onProgressChanged(progress: Int, fromUser: Boolean)
    }

    interface OnControlsEventListener {
        fun onEvent(event: PlayerControlsEvent)
    }

    companion object {
        const val PROGRESS_BAR_MAX = 100
        const val DEFAULT_AUTO_HIDE_TIMEOUT = 3500L
    }

}