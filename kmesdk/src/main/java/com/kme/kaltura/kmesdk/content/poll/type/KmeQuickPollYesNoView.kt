package com.kme.kaltura.kmesdk.content.poll.type

import android.content.Context
import android.util.AttributeSet
import com.kme.kaltura.kmesdk.databinding.LayoutPollYesNoBinding
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType

class KmeQuickPollYesNoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KmeQuickPollTypeView(context, attrs, defStyleAttr) {

    private var binding: LayoutPollYesNoBinding? = null

    init {
        binding = LayoutPollYesNoBinding.inflate(layoutInflater, this, true)

        binding?.apply {
            btnYes.setOnClickListener {
                listener?.onAnswered(type, 0)
            }
            btnNo.setOnClickListener {
                listener?.onAnswered(type, 1)
            }
        }
    }

    override fun onDetachedFromWindow() {
        binding = null
        super.onDetachedFromWindow()
    }

    override val type: KmeQuickPollType = KmeQuickPollType.YES_NO

}