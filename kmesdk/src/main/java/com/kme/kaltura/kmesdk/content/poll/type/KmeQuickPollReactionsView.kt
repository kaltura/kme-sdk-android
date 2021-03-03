package com.kme.kaltura.kmesdk.content.poll.type

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.kme.kaltura.kmesdk.databinding.LayoutPollMultipleChoiceBinding
import com.kme.kaltura.kmesdk.databinding.LayoutPollReactionsBinding
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType

class KmeQuickPollReactionsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KmeQuickPollTypeView(context, attrs, defStyleAttr) {

    private var binding: LayoutPollReactionsBinding? = null

    init {
        binding = LayoutPollReactionsBinding.inflate(layoutInflater, this, true)

        binding?.apply {
            btnReaction0.setOnClickListener {
                listener?.onAnswered(type, 0)
            }
            btnReaction1.setOnClickListener {
                listener?.onAnswered(type, 1)
            }
            btnReaction2.setOnClickListener {
                listener?.onAnswered(type, 2)
            }
        }
    }

    override fun onDetachedFromWindow() {
        binding = null
        super.onDetachedFromWindow()
    }

    override val type: KmeQuickPollType = KmeQuickPollType.REACTIONS

}