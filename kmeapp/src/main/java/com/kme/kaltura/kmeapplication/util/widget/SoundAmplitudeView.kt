package com.kme.kaltura.kmeapplication.util.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.kme.kaltura.kmeapplication.databinding.LayoutSoundAmplitudeViewBinding

class SoundAmplitudeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var binding: LayoutSoundAmplitudeViewBinding =
        LayoutSoundAmplitudeViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setValue(value: Int) {
        if (value in 0..100) {
            val level = ROWS_COUNT - (value * ROWS_COUNT) / MAX_VALUE
            with(binding.root) {
                for (i in childCount - 1 downTo MIN_VALUE) {
                    getChildAt(i).isSelected = level <= i
                }
            }
        }
    }

    companion object {
        const val MIN_VALUE = 0
        const val MAX_VALUE = 100
        const val ROWS_COUNT = 5
    }

}