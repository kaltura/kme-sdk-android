package com.kme.kaltura.kmesdk.content.poll.type

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.databinding.LayoutPollBtnBinding
import com.kme.kaltura.kmesdk.databinding.LayoutPollReactionsBinding
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType

class KmeQuickPollReactionsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KmeQuickPollTypeView<LayoutPollReactionsBinding>(context, attrs, defStyleAttr) {

    init {
        binding?.apply {

            styleView(btnReaction0, 0)
            btnReaction0.resizableContainer.setOnClickListener {
                it.performAnswerJob(0)
            }

            styleView(btnReaction1, 1)
            btnReaction1.resizableContainer.setOnClickListener {
                it.performAnswerJob(1)
            }

            styleView(btnReaction2, 2)
            btnReaction2.resizableContainer.setOnClickListener {
                it.performAnswerJob(2)
            }
        }
    }

    override fun getAnswerView(type: Int): View? {
        return when (type) {
            0 -> binding?.btnReaction0?.resizableContainer
            1 -> binding?.btnReaction1?.resizableContainer
            2 -> binding?.btnReaction2?.resizableContainer
            else -> null
        }
    }

    override fun getViewBinding() = LayoutPollReactionsBinding.inflate(layoutInflater, this, true)

    override val type: KmeQuickPollType = KmeQuickPollType.REACTIONS

    companion object {
        fun styleView(binding: LayoutPollBtnBinding, answerType: Int) {
            val icon = when (answerType) {
                0 -> R.drawable.ic_poll_satisfied
                1 -> R.drawable.ic_poll_neutral
                else -> R.drawable.ic_poll_dissatisfied
            }
            binding.ivIcon.setImageResource(icon)
        }
    }

}