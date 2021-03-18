package com.kme.kaltura.kmesdk.content.poll.type

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.databinding.LayoutPollBtnRatingBinding
import com.kme.kaltura.kmesdk.databinding.LayoutPollRatingBinding
import com.kme.kaltura.kmesdk.util.widget.KmeRatingBar
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType

@SuppressLint("ClickableViewAccessibility")
class KmeQuickPollRatingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KmeQuickPollTypeView<LayoutPollRatingBinding>(context, attrs, defStyleAttr) {

    init {
        binding?.apply {
            ratingBar.setOnRatingSliderChangeListener(object : KmeRatingBar.OnRatingSliderChangeListener {
                override fun onPendingRating(rating: Int) {
                }

                override fun onFinalRating(rating: Int) {
                    ratingBar.performAnswerJob(rating.dec())
                }

                override fun onCancelRating() {
                }
            })
        }
    }

    override fun getViewBinding() = LayoutPollRatingBinding.inflate(layoutInflater, this, true)

    override val type: KmeQuickPollType = KmeQuickPollType.RATING

    companion object {
        fun LayoutPollBtnRatingBinding.styleView(
            answerType: Int
        ) {
            ivIcon.setImageResource(R.drawable.ic_poll_star)
            tvRating.text = answerType.inc().toString()
        }
    }

}