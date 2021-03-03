package com.kme.kaltura.kmesdk.content.poll

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.databinding.LayoutPollResultsBinding
import com.kme.kaltura.kmesdk.util.TopCurvedEdgeTreatment
import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage

class KmeQuickPollResultsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IKmeQuickPollResultsView {

    var closeListener: OnCloseResultsListener? = null

    private var binding: LayoutPollResultsBinding? = null

    init {
        binding = LayoutPollResultsBinding.inflate(LayoutInflater.from(context), this, true)

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

    override fun init(payload: KmeQuickPollModuleMessage.QuickPollEndedPayload) {
    }

    override fun onDetachedFromWindow() {
        binding = null
        super.onDetachedFromWindow()
    }

    interface OnCloseResultsListener {
        fun onCloseResultsView()
    }
}