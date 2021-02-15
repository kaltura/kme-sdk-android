package com.kme.kaltura.kmeapplication.view.view.content.controls

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.forEach
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.util.extensions.gone
import com.kme.kaltura.kmeapplication.util.extensions.isVisible
import com.kme.kaltura.kmeapplication.util.extensions.visible
import com.kme.kaltura.kmeapplication.view.view.content.controls.PlayerControlsEvent.*
import kotlinx.android.synthetic.main.layout_media_view_controls.view.*


class VideoControlsView : BaseControlsView {

    private val accentColor: Int

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        LayoutInflater.from(context).inflate(R.layout.layout_media_view_controls, this)

        accentColor = ContextCompat.getColor(context, R.color.colorAccent)
        setupControlsClickHandler()
        setupSeekBar()
    }

    private fun setupControlsClickHandler() {
        ivPlayPause.setOnClickListener {
            if (isPlaying) {
                controlsEventListener?.onEvent(PAUSE)
            } else {
                controlsEventListener?.onEvent(PLAY)
            }
            isPlaying = !isPlaying
        }
    }

    private fun setupSeekBar() {
        DrawableCompat.setTint(seekBar.thumb, accentColor)
        DrawableCompat.setTint(seekBar.progressDrawable, accentColor)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBarChangeListener?.onProgressChanged(progress, fromUser)
            }
        })
    }

    override fun isControlsVisible(): Boolean = controlsFrame.isVisible()

    override fun setControlsMode(isEnabled: Boolean) {
        controlsFrame.isEnabled = isEnabled
        controlsFrame.forEach {
            setControlsMode(isEnabled, it)
        }
    }

    private fun setControlsMode(enable: Boolean, view: View) {
        if (view is ViewGroup) {
            view.forEach { child ->
                child.isEnabled = enable
                if (child is ViewGroup) {
                    setControlsMode(enable, child)
                }
            }
        } else {
            view.isEnabled = enable
        }
    }

    override fun setProgressBarVisibility(toShow: Boolean) {
        if (toShow) {
            progressBar.visible()
        } else {
            progressBar.gone()
        }
    }

    override fun setSeekBarVisibility(toShow: Boolean) {
        if (toShow) {
            seekBar.visible()
        } else {
            seekBar.gone()
        }
    }

    override fun setSeekBarProgress(progress: Int) {
        seekBar.progress = progress
    }

    override fun setSeekBarMode(isEnabled: Boolean) {
        seekBar.isEnabled = isEnabled
    }

    override fun setSeekBarSecondaryProgress(secondaryProgress: Int) {
        seekBar.secondaryProgress = secondaryProgress
    }

    override fun setPlayPauseVisibility(toShow: Boolean) {
        if (toShow) {
            ivPlayPause.visible()
        } else {
            ivPlayPause.gone()
        }
    }

    override fun setTimeVisibility(toShow: Boolean) {
        if (toShow) {
            tvDuration.visible()
        } else {
            tvDuration.gone()
        }
    }

    override fun setTimePosition(timeSeconds: Long, durationSeconds: Long) {
        val currentTime = formatTime(timeSeconds)
        val durationTime = formatTime(durationSeconds)

        setSeekBarProgress(progressBarValue(timeSeconds, durationSeconds))

        tvDuration.text =
            String.format(context.getString(R.string.media_time_format, currentTime, durationTime))
    }

    override fun updateUI(event: PlayerControlsEvent) {
        when (event) {
            PAUSE, STOPPED -> {
                isPlaying = false
                ivPlayPause.setImageResource(R.drawable.ic_play_circle)
                resetAutoHideControls()
            }
            PLAY, PLAYING -> {
                isPlaying = true
                ivPlayPause.setImageResource(R.drawable.ic_pause_circle)
                resetAutoHideControls()
            }
        }
    }

    override fun setControlsVisibility(doShow: Boolean) {
        super.setControlsVisibility(doShow)
        if (doShow) {
            controlsFrame.visible()
        } else {
            controlsFrame.gone()
        }
    }
}