package com.kme.kaltura.kmesdk.content.playkit.controls

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
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.content.playkit.controls.PlayerControlsEvent.*
import com.kme.kaltura.kmesdk.databinding.LayoutMediaViewControlsBinding
import com.kme.kaltura.kmesdk.gone
import com.kme.kaltura.kmesdk.isVisible
import com.kme.kaltura.kmesdk.visible

class VideoControlsView : BaseControlsView {

    private val accentColor: Int = ContextCompat.getColor(context, R.color.colorAccent)
    private var binding: LayoutMediaViewControlsBinding =
        LayoutMediaViewControlsBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setupControlsClickHandler()
        setupSeekBar()
    }

    private fun setupControlsClickHandler() {
        binding.ivPlayPause.setOnClickListener {
            if (isPlaying) {
                controlsEventListener?.onEvent(PAUSE)
            } else {
                controlsEventListener?.onEvent(PLAY)
            }
            isPlaying = !isPlaying
        }
    }

    private fun setupSeekBar() {
        with(binding) {
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
    }

    override fun isControlsVisible(): Boolean = binding.controlsFrame.isVisible()

    override fun setControlsMode(isEnabled: Boolean) {
        with(binding) {
            controlsFrame.isEnabled = isEnabled
            controlsFrame.forEach {
                setControlsMode(isEnabled, it)
            }
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
            binding.progressBar.visible()
        } else {
            binding.progressBar.gone()
        }
    }

    override fun setSeekBarVisibility(toShow: Boolean) {
        if (toShow) {
            binding.seekBar.visible()
        } else {
            binding.seekBar.gone()
        }
    }

    override fun setSeekBarProgress(progress: Int) {
        binding.seekBar.progress = progress
    }

    override fun setSeekBarMode(isEnabled: Boolean) {
        binding.seekBar.isEnabled = isEnabled
    }

    override fun setSeekBarSecondaryProgress(secondaryProgress: Int) {
        binding.seekBar.secondaryProgress = secondaryProgress
    }

    override fun setPlayPauseVisibility(toShow: Boolean) {
        if (toShow) {
            binding.ivPlayPause.visible()
        } else {
            binding.ivPlayPause.gone()
        }
    }

    override fun setTimeVisibility(toShow: Boolean) {
        if (toShow) {
            binding.tvDuration.visible()
        } else {
            binding.tvDuration.gone()
        }
    }

    override fun setTimePosition(timeSeconds: Long, durationSeconds: Long) {
        val currentTime = formatTime(timeSeconds)
        val durationTime = formatTime(durationSeconds)

        setSeekBarProgress(progressBarValue(timeSeconds, durationSeconds))

        binding.tvDuration.text =
            String.format(context.getString(R.string.media_time_format, currentTime, durationTime))
    }

    override fun updateUI(event: PlayerControlsEvent) {
        when (event) {
            PAUSE, STOPPED -> {
                isPlaying = false
                binding.ivPlayPause.setImageResource(R.drawable.ic_play_circle)
                resetAutoHideControls()
            }
            PLAY, PLAYING -> {
                isPlaying = true
                binding.ivPlayPause.setImageResource(R.drawable.ic_pause_circle)
                resetAutoHideControls()
            }
        }
    }

    override fun setControlsVisibility(doShow: Boolean) {
        super.setControlsVisibility(doShow)
        if (doShow) {
            binding.controlsFrame.visible()
        } else {
            binding.controlsFrame.gone()
        }
    }

}
