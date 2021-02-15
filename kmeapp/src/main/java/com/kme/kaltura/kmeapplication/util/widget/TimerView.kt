package com.kme.kaltura.kmeapplication.util.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.kme.kaltura.kmeapplication.R
import java.util.concurrent.TimeUnit

class TimerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    fun setSeconds(seconds: Long) {
        var elapsedSeconds = seconds
        val secondsInMinute = TimeUnit.MINUTES.toSeconds(1)
        val secondsInHour = TimeUnit.HOURS.toSeconds(1)

        val elapsedHours = elapsedSeconds / secondsInHour
        elapsedSeconds %= secondsInHour
        val elapsedMinutes = elapsedSeconds / secondsInMinute
        elapsedSeconds %= secondsInMinute

        val pattern = context.getString(R.string.recording_timer_pattern)
        text = String.format(pattern, elapsedHours, elapsedMinutes, elapsedSeconds)
    }

}
