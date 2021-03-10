package com.kme.kaltura.kmesdk.content.poll.type

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.databinding.LayoutPollBtnYesNoBinding
import com.kme.kaltura.kmesdk.databinding.LayoutPollYesNoBinding
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType

class KmeQuickPollYesNoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KmeQuickPollTypeView(context, attrs, defStyleAttr) {

    private var binding: LayoutPollYesNoBinding? = null

    init {
        binding = LayoutPollYesNoBinding.inflate(layoutInflater, this, true)

        binding?.apply {

            btnYes.styleView(context, 0)
            btnYes.root.setOnClickListener {
                listener?.onAnswered(type, 0)
            }

            btnNo.styleView(context, 1)
            btnNo.root.setOnClickListener {
                listener?.onAnswered(type, 1)
            }
        }
    }

    override fun onDetachedFromWindow() {
        binding = null
        super.onDetachedFromWindow()
    }

    override val type: KmeQuickPollType = KmeQuickPollType.YES_NO

    companion object {
        fun LayoutPollBtnYesNoBinding.styleView(context: Context, answerType: Int) {
            val icon = if (answerType == 0)
                R.drawable.ic_poll_yes
            else
                R.drawable.ic_poll_no

            val color = if (answerType == 0)
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
            else
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange))

            ContextCompat.getDrawable(context, icon)?.let {
                ivIcon.setImageDrawable(it)
            }
            ivIcon.backgroundTintList = color
        }
    }

}