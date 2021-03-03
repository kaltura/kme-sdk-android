package com.kme.kaltura.kmesdk.content.poll.type

import android.content.Context
import android.util.AttributeSet
import com.kme.kaltura.kmesdk.databinding.LayoutPollRatingBinding
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType

class KmeQuickPollRatingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KmeQuickPollTypeView(context, attrs, defStyleAttr) {

    private var binding: LayoutPollRatingBinding? = null

    init {
        binding = LayoutPollRatingBinding.inflate(layoutInflater, this, true)

        binding?.apply {
            ratingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
                listener?.onAnswered(type, rating.toInt())
            }
        }
    }

    override fun onDetachedFromWindow() {
        binding = null
        super.onDetachedFromWindow()
    }

    override val type: KmeQuickPollType = KmeQuickPollType.RATING

}