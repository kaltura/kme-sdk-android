package com.kme.kaltura.kmesdk.content.poll.type

import android.content.Context
import android.util.AttributeSet
import com.kme.kaltura.kmesdk.databinding.LayoutPollMultipleChoiceBinding
import com.kme.kaltura.kmesdk.databinding.LayoutPollYesNoBinding
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType

class KmeQuickPollMultipleChoiceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KmeQuickPollTypeView(context, attrs, defStyleAttr) {

    private var binding: LayoutPollMultipleChoiceBinding? = null

    init {
        binding = LayoutPollMultipleChoiceBinding.inflate(layoutInflater, this, true)

        binding?.apply {
            btnChoiceA.setOnClickListener {
                listener?.onAnswered(type, 0)
            }
            btnChoiceB.setOnClickListener {
                listener?.onAnswered(type, 1)
            }
            btnChoiceC.setOnClickListener {
                listener?.onAnswered(type, 2)
            }
            btnChoiceD.setOnClickListener {
                listener?.onAnswered(type, 3)
            }
        }
    }

    override fun onDetachedFromWindow() {
        binding = null
        super.onDetachedFromWindow()
    }

    override val type: KmeQuickPollType = KmeQuickPollType.MULTIPLE_CHOICE

}