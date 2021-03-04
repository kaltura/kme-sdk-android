package com.kme.kaltura.kmesdk.content.poll

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.databinding.LayoutPollResultsBinding
import com.kme.kaltura.kmesdk.util.TopCurvedEdgeTreatment
import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage.QuickPollEndedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage.QuickPollStartedPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType.*

class KmeQuickPollResultsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IKmeQuickPollResultsView {

    var closeListener: OnCloseResultsListener? = null

    private var binding: LayoutPollResultsBinding? = null

    private val layoutInflater by lazy { LayoutInflater.from(context) }

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
        endPollPayload: QuickPollEndedPayload
    ) {
        val userCount = currentPollPayload.userCount ?: 0
        val resultsCount = endPollPayload.answers?.size ?: 0
        val resultsPercentCount = resultsCount / userCount * 100

        binding?.tvAnonymousPoll?.visibility = if (currentPollPayload.isAnonymous == true)
            VISIBLE
        else
            GONE

        if (currentPollPayload.type == RATING) {
            val averageResult = resultsCount / userCount * 100
            binding?.groupAverageResult?.visibility = VISIBLE
            binding?.tvAverageResultNumber?.text = averageResult.toString()
        } else {
            binding?.groupAverageResult?.visibility = GONE
        }

        binding?.tvResultsCount?.text = resultsCount.toString()
        binding?.tvResults?.text = String.format(
            resources.getString(R.string.quick_poll_average_result), userCount, resultsPercentCount
        )

        setupProgressViews(currentPollPayload, endPollPayload)
    }

    private fun setupProgressViews(
        currentPollPayload: QuickPollStartedPayload,
        endPollPayload: QuickPollEndedPayload
    ) {
        val viewsCount = when (currentPollPayload.type) {
            YES_NO -> 2
            REACTIONS -> 3
            RATING -> 5
            MULTIPLE_CHOICE -> 4
            else -> 0
        }

        for (i in 0..viewsCount) {
            generateProgressView()
        }
    }

    private fun generateIconBitmap(pollType: KmeQuickPollType, answerType: Int) : Bitmap {
        return when (pollType) {
            YES_NO -> {
                if (answerType == 0) {
                    initializeIconView(R.layout.layout_poll_btn_yes)
                } else {
                    initializeIconView(R.layout.layout_poll_btn_no)
                }
            }
            REACTIONS -> 3
            RATING -> 5
            MULTIPLE_CHOICE -> 4
            else -> 0
        }
    }

    private fun initializeIconView(@LayoutRes layout: Int) : Bitmap  {
       val view = layoutInflater.inflate(layout, null, false)
    }

    private fun generateProgressView(iconBitmap: Bitmap, answersCount: Int): KmeQuickPollProgressBar {
        return KmeQuickPollProgressBar(context, null, R.style.QuickPollProgressBar_Default).apply {
            setIcon(iconBitmap)
            setPrefix(answersCount.toString())
        }
    }

    override fun onDetachedFromWindow() {
        binding = null
        super.onDetachedFromWindow()
    }

    interface OnCloseResultsListener {
        fun onCloseResultsView()
    }
}