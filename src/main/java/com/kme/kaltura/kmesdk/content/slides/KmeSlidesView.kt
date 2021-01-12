package com.kme.kaltura.kmesdk.content.slides

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.glide
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.ActiveContentPayload.Slide
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.WhiteboardPayload
import kotlinx.android.synthetic.main.layout_slides_view.view.*

class KmeSlidesView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), IKmeSlidesListener {

    private lateinit var config: Config

    private var slides: MutableList<Slide> = mutableListOf()
    private var slidesAdapter: SlidesAdapter? = null
    private var selectedSlide: Slide? = null

    private var originalImageSize: Size? = null


    init {
        LayoutInflater.from(context).inflate(R.layout.layout_slides_view, this)
    }

    override fun init(config: Config) {
        this.config = config

        fabZoomIn.setOnClickListener {
//            zoomLayout.zoomIn()
//            drawing.setErase(true)
        }
        fabZoomOut.setOnClickListener {
//            zoomLayout.zoomOut()

//            drawing.setErase(false)

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
        originalImageSize = null

        selectedSlide?.let {
            ivSlide.glide(it.url, config.cookie, config.fileUrl) { originalSize ->
                originalImageSize = originalSize
                setupWhiteboardView()
            }
        }
    }

    private fun setupWhiteboardView() {
        ivSlide.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val drawable = ivSlide.drawable
                if (drawable != null) {
                    val imageBounds = RectF()
                    ivSlide.imageMatrix.mapRect(imageBounds, RectF(drawable.bounds))

                    originalImageSize?.let { imageSize ->
                        init(imageSize, imageBounds)
                    }
                    ivSlide.viewTreeObserver.removeOnPreDrawListener(this)
                }
                return true
            }
        })
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

    override fun init(originalImageSize: Size, imageBounds: RectF) {
        whiteboardLayout.init(originalImageSize, imageBounds)
    }

    override fun setDrawings(drawings: List<WhiteboardPayload.Drawing>) {
        whiteboardLayout.setDrawings(drawings)
    }

    override fun addDrawing(drawing: WhiteboardPayload.Drawing) {
        whiteboardLayout.addDrawing(drawing)
    }

    override fun removeDrawing(layer: String) {
        whiteboardLayout.removeDrawing(layer)
    }

    override fun removeDrawings() {
        whiteboardLayout.removeDrawings()
    }

    class Config(
        val cookie: String,
        val fileUrl: String,
    ) {
        var currentSlide: Int = 0
    }

}