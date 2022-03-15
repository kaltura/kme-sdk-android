package com.kme.kaltura.kmesdk.content.poll.type

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.databinding.LayoutPollBtnBinding
import com.kme.kaltura.kmesdk.databinding.LayoutPollMultipleChoiceBinding
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType

class KmeQuickPollMultipleChoiceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KmeQuickPollTypeView<LayoutPollMultipleChoiceBinding>(context, attrs, defStyleAttr) {

    init {
        binding?.apply {

            styleView(btnChoiceA, 0)
            btnChoiceA.resizableContainer.setOnClickListener {
                it.performAnswerJob(0)
            }

            styleView(btnChoiceB, 1)
            btnChoiceB.resizableContainer.setOnClickListener {
                it.performAnswerJob(1)
            }

            styleView(btnChoiceC, 2)
            btnChoiceC.resizableContainer.setOnClickListener {
                it.performAnswerJob(2)
            }

            styleView(btnChoiceD, 3)
            btnChoiceD.resizableContainer.setOnClickListener {
                it.performAnswerJob(3)
            }
        }
    }

    override fun getAnswerView(type: Int): View? {
        return when (type) {
            0 -> binding?.btnChoiceA?.resizableContainer
            1 -> binding?.btnChoiceB?.resizableContainer
            2 -> binding?.btnChoiceC?.resizableContainer
            3 -> binding?.btnChoiceD?.resizableContainer
            else -> null
        }
    }

    override fun getViewBinding() = LayoutPollMultipleChoiceBinding.inflate(layoutInflater, this, true)

    override val type: KmeQuickPollType = KmeQuickPollType.MULTIPLE_CHOICE

    companion object {
        fun styleView(
            binding: LayoutPollBtnBinding,
            answerType: Int
        ) {
            val icon = when (answerType) {
                0 -> R.drawable.ic_poll_reaction_a
                1 -> R.drawable.ic_poll_reaction_b
                2 -> R.drawable.ic_poll_reaction_c
                else -> R.drawable.ic_poll_reaction_d
            }
            binding.ivIcon.setImageResource(icon)
        }
    }

}