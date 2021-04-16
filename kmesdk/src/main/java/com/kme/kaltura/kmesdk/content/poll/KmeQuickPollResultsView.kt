package com.kme.kaltura.kmesdk.content.poll

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.content.poll.type.KmeQuickPollMultipleChoiceView
import com.kme.kaltura.kmesdk.content.poll.type.KmeQuickPollRatingView.Companion.styleView
import com.kme.kaltura.kmesdk.content.poll.type.KmeQuickPollReactionsView
import com.kme.kaltura.kmesdk.content.poll.type.KmeQuickPollYesNoView
import com.kme.kaltura.kmesdk.databinding.LayoutPollBtnBinding
import com.kme.kaltura.kmesdk.databinding.LayoutPollBtnRatingBinding
import com.kme.kaltura.kmesdk.databinding.LayoutPollResultsBinding
import com.kme.kaltura.kmesdk.getBitmapFromView
import com.kme.kaltura.kmesdk.util.TopCurvedEdgeTreatment
import com.kme.kaltura.kmesdk.util.widget.KmeProgressBar
import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType.*

class KmeQuickPollResultsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IKmeQuickPollResultsView {

    var closeListener: OnCloseResultsListener? = null

    private var currentPollPayload: QuickPollStartedPayload? = null
    private var answers = mutableListOf<QuickPollPayload.Answer>()

    private var binding: LayoutPollResultsBinding? = null

    private val layoutInflater by lazy { LayoutInflater.from(context) }
    private val progressViewPadding by lazy {
        resources.getDimensionPixelSize(R.dimen.quick_poll_results_item_padding)
    }
    private val iconLayoutParams by lazy {
        LayoutParams(
            resources.getDimensionPixelSize(R.dimen.quick_poll_results_icon_size),
            resources.getDimensionPixelSize(R.dimen.quick_poll_results_icon_size)
        ).apply {
            gravity = Gravity.CENTER
        }
    }

    init {
        binding = LayoutPollResultsBinding.inflate(layoutInflater, this, true)

        val shapeModel = ShapeAppearanceModel.builder()
            .setTopEdge(TopCurvedEdgeTreatment(
                resources.getDimension(R.dimen.quick_poll_fab_cradle_margin),
                resources.getDimension(R.dimen.quick_poll_fab_cradle_corner_radius),
                resources.getDimension(R.dimen.quick_poll_cradle_vertical_offset)
            ).apply {
                fabDiameter = resources.getDimension(R.dimen.quick_poll_fab_size)
            })
            .setAllCornerSizes(resources.getDimension(R.dimen.quick_poll_results_view_corner_radius))
            .build()

        val materialShapeDrawable = MaterialShapeDrawable(shapeModel).apply {
            setTint(ContextCompat.getColor(context, android.R.color.black))
        }
        val borderShapeDrawable = MaterialShapeDrawable(shapeModel).apply {
            setTint(ContextCompat.getColor(context, R.color.grey1))
        }

        binding?.resultsContainer?.background = materialShapeDrawable
        binding?.borders?.background = borderShapeDrawable
        binding?.fabClose?.setOnClickListener { closeListener?.onCloseResultsView() }
    }

    override fun init(
        currentPollPayload: QuickPollStartedPayload,
        endPollPayload: QuickPollEndedPayload?
    ) {
        this.currentPollPayload = currentPollPayload
        binding?.tvAnonymousPoll?.visibility = if (currentPollPayload.isAnonymous == true)
            VISIBLE
        else
            GONE

        applyAnswers(endPollPayload?.answers)
    }

    private fun updateRatingViewInfo(
        averageResult: Float,
        userCount: Int,
        resultsCount: Int
    ) {
        val resultsPercentCount = (resultsCount.toFloat() / userCount.toFloat() * 100).toInt()

        binding?.groupAverageResult?.visibility = VISIBLE
        binding?.tvAverageResultNumber?.text = "%.2f".format(averageResult)
        binding?.tvResultsCount?.text = resultsCount.toString()
        binding?.tvResults?.text = String.format(
            resources.getString(R.string.quick_poll_average_result),
            userCount,
            resultsPercentCount
        )
    }

    override fun applyAnswer(answer: QuickPollPayload.Answer) {
        val progressViewsCount = binding?.progressContainer?.childCount ?: 0
        if (progressViewsCount == 0) {
            applyAnswers(arrayListOf(answer))
        } else {
            answers.add(answer)
            for (index in 0..progressViewsCount) {
                val progressView = getProgressView(index)
                progressView?.let {
                    val answersTypeCount =
                        answers.filter { answered -> answered.answer == index }.size
                    val progress =
                        (answersTypeCount.toFloat() / answers.size.toFloat() * 100).toInt()

                    progressView.setPrefix(
                        String.format(
                            resources.getString(R.string.quick_poll_answers_count_prefix),
                            answersTypeCount
                        )
                    )
                    progressView.applyProgress(progress, true)
                }
            }

            if (currentPollPayload?.type == RATING) {
                updateRatingViewInfo(
                    this.answers.getAverageRating(),
                    currentPollPayload?.userCount ?: 0,
                    this.answers.size
                )
            } else {
                binding?.groupAverageResult?.visibility = GONE
            }
        }
    }

    private fun getProgressView(index: Int?): KmeProgressBar? {
        if (index == null) return null
        val view = binding?.progressContainer?.getChildAt(index)
        return if (view is KmeProgressBar)
            return view
        else
            null
    }

    override fun applyAnswers(answers: List<QuickPollPayload.Answer>?) {
        this.answers.clear()

        if (answers != null) {
            this.answers.addAll(answers)
        }

        val pollType = currentPollPayload?.type ?: return
        val answersCount = this.answers.size

        val viewsCount = when (pollType) {
            YES_NO -> 2
            REACTIONS -> 3
            RATING -> 5
            MULTIPLE_CHOICE -> 4
        }

        binding?.progressContainer?.removeAllViews()

        for (index in 0 until viewsCount) {
            val answersTypeCount =
                this.answers.filter { answer -> answer.answer == index }.size
            val progress = (answersTypeCount.toFloat() / answersCount.toFloat() * 100).toInt()
            val bitmap = generateIconBitmap(pollType, index)
            val progressView = generateProgressView(bitmap, answersTypeCount)
            progressView.setPadding(0, progressViewPadding, 0, progressViewPadding)

            binding?.progressContainer?.addView(progressView)

            progressView.applyProgress(progress, true)
        }

        if (currentPollPayload?.type == RATING) {
            updateRatingViewInfo(
                this.answers.getAverageRating(),
                currentPollPayload?.userCount ?: 0,
                this.answers.size
            )
        } else {
            binding?.groupAverageResult?.visibility = GONE
        }
    }

    private fun generateIconBitmap(
        pollType: KmeQuickPollType,
        answerType: Int
    ): Bitmap? {
        val view = when (pollType) {
            YES_NO -> LayoutPollBtnBinding.inflate(layoutInflater).apply {
                KmeQuickPollYesNoView.styleView(this, answerType)
            }
            REACTIONS -> LayoutPollBtnBinding.inflate(layoutInflater).apply {
                KmeQuickPollReactionsView.styleView(this, answerType)
            }
            RATING -> LayoutPollBtnRatingBinding.inflate(layoutInflater).apply {
                styleView(answerType)
            }
            MULTIPLE_CHOICE -> LayoutPollBtnBinding.inflate(layoutInflater).apply {
                KmeQuickPollMultipleChoiceView.styleView(this, answerType)
            }
        }.root

        view.layoutParams = iconLayoutParams
        return view.getBitmapFromView()
    }

    private fun generateProgressView(
        iconBitmap: Bitmap?,
        answersCount: Int
    ): KmeProgressBar {
        return KmeProgressBar(context, null, R.style.QuickPollProgressBar_Default).apply {
            iconBitmap?.let {
                setIcon(it)
            }
            setPrefix(
                String.format(
                    resources.getString(R.string.quick_poll_answers_count_prefix),
                    answersCount
                )
            )
        }
    }

    private fun List<QuickPollPayload.Answer>?.getAverageRating(): Float {
        if (isNullOrEmpty()) return 0f

        val starsCount = 5
        val totalAnswers = size
        val maxRating = totalAnswers * starsCount
        val sumOfRating = sumBy { it.answer?.inc() ?: 0 }.toFloat()

        return sumOfRating * starsCount / maxRating
    }

    override fun onDetachedFromWindow() {
        binding = null
        super.onDetachedFromWindow()
    }

    interface OnCloseResultsListener {
        fun onCloseResultsView()
    }

}