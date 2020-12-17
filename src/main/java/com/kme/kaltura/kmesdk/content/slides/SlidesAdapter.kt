package com.kme.kaltura.kmesdk.content.slides

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.glide
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.ActiveContentPayload.Slide
import kotlinx.android.synthetic.main.item_slide_layout.view.*

class SlidesAdapter(
    val cookie: String?,
    val filesUrl: String?,
) : RecyclerView.Adapter<SlidesAdapter.SlideHolder>() {

    private val slides = mutableListOf<Slide>()

    var onSlideClick: ((slide: Slide) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_slide_layout, parent, false)
        return SlideHolder(view)
    }

    override fun onBindViewHolder(holder: SlideHolder, position: Int) {
        holder.bind(slides[position])
    }

    override fun getItemCount() = slides.size

    fun setData(data: List<Slide>) {
        slides.clear()
        slides.addAll(data)
        notifyDataSetChanged()
    }

    inner class SlideHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(slide: Slide) {
            with(itemView) {
                root.isSelected = slide.isSelected
                tvSlideNumber.text = slide.slideNumber ?: adapterPosition.toString()
                ivSlide.glide(slide.url, cookie, filesUrl)

                setOnClickListener {
                    onSlideClick?.invoke(slide)
                }
            }
        }
    }
}