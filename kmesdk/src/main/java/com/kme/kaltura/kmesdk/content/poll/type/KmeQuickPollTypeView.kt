package com.kme.kaltura.kmesdk.content.poll.type

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.viewbinding.ViewBinding
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.databinding.LayoutPollMultipleChoiceBinding
import com.kme.kaltura.kmesdk.databinding.LayoutPollRatingBinding
import com.kme.kaltura.kmesdk.databinding.LayoutPollReactionsBinding
import com.kme.kaltura.kmesdk.databinding.LayoutPollYesNoBinding
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType

/**
 * An abstract class for different QuickPoll view types.
 */
abstract class KmeQuickPollTypeView<B : ViewBinding> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var listener: OnAnswerListener? = null

    var awaitAnswerTimeout = 2000L

    var isAnonymousPoll = true

    abstract val type: KmeQuickPollType

    protected var layoutInflater: LayoutInflater = LayoutInflater.from(context)

    protected var binding: B? = null
        private set

    protected abstract fun getViewBinding(): B

    private val inactiveButtonSize by lazy { resources.getDimensionPixelSize(R.dimen.quick_poll_btn_size) }
    private val activeButtonSize by lazy { resources.getDimensionPixelSize(R.dimen.quick_poll_active_btn_size) }

    private val defaultAnimationDuration = 300L

    private var isTimeoutAnimationCanceled = false
    private var prevAnimatedView: View? = null
    private var prevAnswerType: Int? = null

    private val timeoutAnswerAnimation by lazy {
        ValueAnimator.ofInt(0, 100).apply {
            interpolator = LinearInterpolator()
            duration = awaitAnswerTimeout
        }
    }

    private val buttonAnimation by lazy {
        ValueAnimator.ofInt(inactiveButtonSize, activeButtonSize).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = defaultAnimationDuration
        }
    }

    init {
        this.binding = this.getViewBinding()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        getAnonymousLabel()?.visibility = if (isAnonymousPoll) VISIBLE else GONE
    }

    protected fun View.performAnswerJob(answer: Int) {
        if (prevAnswerType != answer) {
            prevAnswerType = answer
            cancelAnswerTimeout()
            startAnswerAnimation(answer)
        }
    }

    private fun cancelAnswerTimeout() {
        buttonAnimation.cancel()
        buttonAnimation.removeAllUpdateListeners()
        buttonAnimation.removeAllListeners()

        timeoutAnswerAnimation.cancel()
        timeoutAnswerAnimation.removeAllListeners()
        timeoutAnswerAnimation.removeAllUpdateListeners()
    }

    private fun View.startAnswerAnimation(answer: Int) {
        buttonAnimation.apply {
            addUpdateListener {
                val params = this@startAnswerAnimation.layoutParams
                params.width = it.animatedValue as Int
                params.height = it.animatedValue as Int
                this@startAnswerAnimation.layoutParams = params
            }
        }

        timeoutAnswerAnimation.apply {
            addUpdateListener {
                getTimeoutProgressBar()?.apply {
                    progress = it.animatedValue as Int
                }
            }

            doOnStart {
                isTimeoutAnimationCanceled = false
                prevAnimatedView = this@startAnswerAnimation
                getTimeoutProgressBar()?.apply {
                    alpha = 0f
                    visibility = VISIBLE
                    animate()
                        .alpha(1f)
                        .setDuration(defaultAnimationDuration)
                        .start()
                }
            }

            doOnCancel {
                isTimeoutAnimationCanceled = true
                val params = prevAnimatedView?.layoutParams
                params?.width = inactiveButtonSize
                params?.height = inactiveButtonSize
                prevAnimatedView?.layoutParams = params
            }

            doOnEnd {
                if (!isTimeoutAnimationCanceled) {
                    listener?.onAnswered(type, answer)
                }
            }
        }

        buttonAnimation.start()
        timeoutAnswerAnimation.start()
    }

    private fun getTimeoutProgressBar(): ProgressBar? {
        return when (binding) {
            is LayoutPollYesNoBinding -> (binding as LayoutPollYesNoBinding).timeoutProgress.root
            is LayoutPollRatingBinding -> (binding as LayoutPollRatingBinding).timeoutProgress.root
            is LayoutPollMultipleChoiceBinding -> (binding as LayoutPollMultipleChoiceBinding).timeoutProgress.root
            is LayoutPollReactionsBinding -> (binding as LayoutPollReactionsBinding).timeoutProgress.root
            else -> null
        }
    }

    private fun getAnonymousLabel(): AppCompatTextView? {
        return when (binding) {
            is LayoutPollYesNoBinding -> (binding as LayoutPollYesNoBinding).tvAnonymousPoll.root
            is LayoutPollRatingBinding -> (binding as LayoutPollRatingBinding).tvAnonymousPoll.root
            is LayoutPollMultipleChoiceBinding -> (binding as LayoutPollMultipleChoiceBinding).tvAnonymousPoll.root
            is LayoutPollReactionsBinding -> (binding as LayoutPollReactionsBinding).tvAnonymousPoll.root
            else -> null
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancelAnswerTimeout()
        binding = null
    }

    /**
     * A callback that notifies when the user made click on the answer button.
     */
    interface OnAnswerListener {
        /**
         * Notification that the user has selected a final answer.
         *
         * @param type the final answer type. Described in [KmeQuickPollType]
         * @param answer the final answer selected.
         */
        fun onAnswered(type: KmeQuickPollType, answer: Int)
    }

    companion object {

        /**
         * Utils function for creating a new instance QuickPoll view types.
         *
         * @param context context object needed to inflate views.
         * @param type [KmeQuickPollType] available QuickPoll types.
         *
         * @return [KmeQuickPollTypeView] a new instance QuickPoll view types.
         * Returns null if [KmeQuickPollType] is not supported.
         */
        fun getView(
            context: Context,
            type: KmeQuickPollType?
        ): KmeQuickPollTypeView<out ViewBinding>? {
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