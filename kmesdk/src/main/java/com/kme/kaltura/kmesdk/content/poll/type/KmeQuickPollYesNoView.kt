package com.kme.kaltura.kmesdk.content.poll.type

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.databinding.LayoutPollBtnBinding
import com.kme.kaltura.kmesdk.databinding.LayoutPollYesNoBinding
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType

class KmeQuickPollYesNoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KmeQuickPollTypeView<LayoutPollYesNoBinding>(context, attrs, defStyleAttr) {

    init {
        binding?.apply {
            styleView(btnYes, 0)
            btnYes.root.setOnClickListener {
                it.performAnswerJob(0)
            }

            styleView(btnNo, 1)
            btnNo.root.setOnClickListener {
                it.performAnswerJob(1)
            }
        }
    }

    override fun getViewBinding() =  LayoutPollYesNoBinding.inflate(layoutInflater, this, true)

    override val type: KmeQuickPollType = KmeQuickPollType.YES_NO

    companion object {
        fun styleView(
            binding: LayoutPollBtnBinding,
            answerType: Int
        ) {
            val icon = if (answerType == 0)
                R.drawable.ic_poll_yes
            else
                R.drawable.ic_poll_no

            binding.ivIcon.setImageResource(icon)
        }
    }

}