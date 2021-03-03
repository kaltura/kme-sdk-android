package com.kme.kaltura.kmesdk.content.poll.type

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType

abstract class KmeQuickPollTypeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var listener: OnAnswerListener? = null

    var layoutInflater: LayoutInflater = LayoutInflater.from(context)

    abstract val type: KmeQuickPollType

    interface OnAnswerListener {
        fun onAnswered(type: KmeQuickPollType, answer: Int)
    }

    companion object {
        fun getView(context: Context, type: KmeQuickPollType?): KmeQuickPollTypeView? {
            return when (type) {
                KmeQuickPollType.YES_NO -> KmeQuickPollYesNoView(context)
                KmeQuickPollType.REACTIONS -> KmeQuickPollReactionsView(context)
                KmeQuickPollType.RATING -> KmeQuickPollRatingView(context)
                KmeQuickPollType.MULTIPLE_CHOICE -> KmeQuickPollMultipleChoiceView(context)
                else -> null
            }
        }
    }

}