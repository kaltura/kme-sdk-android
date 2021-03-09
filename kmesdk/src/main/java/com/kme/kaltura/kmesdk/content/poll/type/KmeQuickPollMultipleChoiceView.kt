package com.kme.kaltura.kmesdk.content.poll.type

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.databinding.LayoutPollBtnChoiceBinding
import com.kme.kaltura.kmesdk.databinding.LayoutPollMultipleChoiceBinding
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType

class KmeQuickPollMultipleChoiceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KmeQuickPollTypeView(context, attrs, defStyleAttr) {

    private var binding: LayoutPollMultipleChoiceBinding? = null

    private val textSize by lazy { resources.getDimension(R.dimen.quick_poll_btn_value_text_size) }

    init {
        binding = LayoutPollMultipleChoiceBinding.inflate(layoutInflater, this, true)

        binding?.apply {

            btnChoiceA.styleView(context, 0, textSize)
            btnChoiceA.root.setOnClickListener {
                listener?.onAnswered(type, 0)
            }

            btnChoiceB.styleView(context, 1, textSize)
            btnChoiceB.root.setOnClickListener {
                listener?.onAnswered(type, 1)
            }

            btnChoiceC.styleView(context, 2, textSize)
            btnChoiceC.root.setOnClickListener {
                listener?.onAnswered(type, 2)
            }

            btnChoiceD.styleView(context, 3, textSize)
            btnChoiceD.root.setOnClickListener {
                listener?.onAnswered(type, 3)
            }
        }
    }

    override fun onDetachedFromWindow() {
        binding = null
        super.onDetachedFromWindow()
    }

    override val type: KmeQuickPollType = KmeQuickPollType.MULTIPLE_CHOICE

    companion object {
        fun LayoutPollBtnChoiceBinding.styleView(
            context: Context,
            answerType: Int,
            textSize: Float
        ) {
            tvValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

            val text = when (answerType) {
                0 -> context.getString(R.string.choice_a)
                1 -> context.getString(R.string.choice_b)
                2 -> context.getString(R.string.choice_c)
                else -> context.getString(R.string.choice_d)
            }

            tvValue.text = text
        }
    }

}