package com.kme.kaltura.kmesdk.content.poll.type

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.databinding.LayoutPollBtnReactionBinding
import com.kme.kaltura.kmesdk.databinding.LayoutPollReactionsBinding
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType

class KmeQuickPollReactionsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KmeQuickPollTypeView(context, attrs, defStyleAttr) {

    private var binding: LayoutPollReactionsBinding? = null

    init {
        binding = LayoutPollReactionsBinding.inflate(layoutInflater, this, true)

        binding?.apply {

            btnReaction0.styleView(context, 0)
            btnReaction0.root.setOnClickListener {
                listener?.onAnswered(type, 0)
            }

            btnReaction1.styleView(context, 1)
            btnReaction1.root.setOnClickListener {
                listener?.onAnswered(type, 1)
            }

            btnReaction2.styleView(context, 2)
            btnReaction2.root.setOnClickListener {
                listener?.onAnswered(type, 2)
            }
        }
    }

    override fun onDetachedFromWindow() {
        binding = null
        super.onDetachedFromWindow()
    }

    override val type: KmeQuickPollType = KmeQuickPollType.REACTIONS

    companion object {
        fun LayoutPollBtnReactionBinding.styleView(context: Context, answerType: Int) {
            val icon = when (answerType) {
                0 -> R.drawable.ic_satisfied
                1 -> R.drawable.ic_neutral
                else -> R.drawable.ic_dissatisfied
            }

            val color = when (answerType) {
                0 -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
                1 -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow))
                else -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange))
            }

            ContextCompat.getDrawable(context, icon)?.let {
                ivIcon.setImageDrawable(it)
            }
            ivIcon.backgroundTintList = color
        }
    }

}