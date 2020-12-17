package com.kme.kaltura.kmesdk.content.slides

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.glide
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.ActiveContentPayload.Slide
import kotlinx.android.synthetic.main.layout_slides_view.view.*

class KmeSlidesView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), IKmeSlidesListener {

    private lateinit var config: Config

    private var slides: MutableList<Slide> = mutableListOf()
    private var slidesAdapter: SlidesAdapter? = null
    private var selectedSlide: Slide? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_slides_view, this)
    }

    override fun init(config: Config) {
        this.config = config

        fabZoomIn.setOnClickListener {
            ivSlide.zoomIn()
        }
        fabZoomOut.setOnClickListener {
            ivSlide.zoomOut()
        }
    }

    override fun setSlides(slides: List<Slide>) {
        check(::config.isInitialized) {
            "${javaClass.simpleName} is not initialized."
        }
        this.slides.clear()
        this.slides.addAll(slides)
        this.slides.sortedBy { slide -> slide.slideNumber?.toInt() ?: 0 }

        selectedSlide = getSlideByNumber(config.currentSlide)

        setupContentView()
        setupPreviews()

        selectedSlide?.let {
            notifySlideSelected(it)
        }
    }

    private fun setupContentView() {
        selectedSlide?.let {
            ivSlide.glide(it.url, config.cookie, config.fileUrl)
        }
    }

    private fun setupPreviews() {
        slidesAdapter = SlidesAdapter(config.cookie, config.fileUrl).apply {
            setData(slides)
        }
        rvSlides.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = slidesAdapter
        }
    }

    private fun notifySlideSelected(selectedSlide: Slide) {
        val indexOf = slides.indexOf(selectedSlide)
        Log.e("TAG", "notifySlideSelected: $indexOf")
        if (indexOf >= 0) {
            slides.forEachIndexed { index, slide ->
                slide.isSelected = index == indexOf
            }
            slidesAdapter?.setData(slides)
            rvSlides.post {
                (rvSlides.layoutManager as LinearLayoutManager?)
                    ?.scrollToPositionWithOffset(indexOf, 0)
            }
        }
    }

    private fun getSlideByNumber(number: Int): Slide? {
        return slides.find { slide -> slide.slideNumber?.toInt() == number }
    }

    override val currentSlide: Slide?
        get() = selectedSlide

    override val size: Int
        get() = slides.size

    override fun next() {
        val nextSlideNumber = currentSlide?.slideNumber?.toInt()?.inc()
        nextSlideNumber?.let { number ->
            val nextSlide = getSlideByNumber(number)
            nextSlide?.let { slide ->
                selectedSlide = slide
                setupContentView()
                notifySlideSelected(slide)
            }
        }
    }

    override fun previous() {
        val prevSlideNumber = currentSlide?.slideNumber?.toInt()?.dec()
        prevSlideNumber?.let { number ->
            val prevSlide = getSlideByNumber(number)
            prevSlide?.let { slide ->
                selectedSlide = slide
                setupContentView()
                notifySlideSelected(slide)
            }
        }
    }

    override fun toSlide(slideNumber: Int) {
        val slide = getSlideByNumber(slideNumber)
        slide?.let { it ->
            selectedSlide = it
            setupContentView()
            notifySlideSelected(slide)
        }
    }

    class Config(
        val cookie: String,
        val fileUrl: String,
    ) {
        var currentSlide: Int = 0
    }

}