package com.kme.kaltura.kmesdk.content.poll.type

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.databinding.LayoutPollBtnRatingBinding
import com.kme.kaltura.kmesdk.databinding.LayoutPollRatingBinding
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType
import kotlinx.coroutines.*

@SuppressLint("ClickableViewAccessibility")
class KmeQuickPollRatingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KmeQuickPollTypeView(context, attrs, defStyleAttr) {

    var awaitAnswerTimeout = 1500L

    private var binding: LayoutPollRatingBinding? = null
    private var uiScope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    init {
        binding = LayoutPollRatingBinding.inflate(layoutInflater, this, true)

        binding?.apply {
            ratingBar.setOnTouchListener { v, event ->
                v.onTouchEvent(event)
                if (MotionEvent.ACTION_UP == event?.action) {
                    job?.cancel()
                    job = uiScope.launch {
                        delay(awaitAnswerTimeout)
                        listener?.onAnswered(type, ratingBar.rating.toInt().dec())
                    }
                } else if (MotionEvent.ACTION_DOWN == event?.action) {
                    job?.cancel()
                }
                true
            }
        }
    }

    override fun onDetachedFromWindow() {
        binding = null
        job?.cancel()
        job = null
        super.onDetachedFromWindow()
    }

    override val type: KmeQuickPollType = KmeQuickPollType.RATING

    companion object {
        fun LayoutPollBtnRatingBinding.styleView(
            answerType: Int
        ) {
            ivIcon.setImageResource(R.drawable.ic_star)
            tvRating.text = answerType.inc().toString()
        }
    }

}