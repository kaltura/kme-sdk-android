package com.kme.kaltura.kmesdk.content.slides

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Base64
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.content.whiteboard.KmeWhiteboardView
import com.kme.kaltura.kmesdk.glide
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.ActiveContentPayload.Page
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.ActiveContentPayload.Slide
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.WhiteboardPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType
import kotlinx.android.synthetic.main.layout_slides_view.view.*


class KmeSlidesView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), IKmeSlidesListener {

    private lateinit var config: Config

    private var slides: MutableList<Slide> = mutableListOf()
    private var slidesAdapter: SlidesAdapter? = null
    private var selectedSlide: Slide? = null

    private var pages: MutableList<Page> = mutableListOf()
    private var selectedPage: Page? = null

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

        if (KmeContentType.SLIDES == config.payload.contentType) {
            setSlides(config.payload)
        } else if (KmeContentType.WHITEBOARD == config.payload.contentType) {
            setWhiteboardPages(config.payload)
        }
    }

    private fun setWhiteboardPages(payload: KmeActiveContentModuleMessage.SetActiveContentPayload) {
        payload.metadata.pages?.let {
            rvSlides?.visibility = GONE

            this.pages.clear()
            this.pages.addAll(it)

            selectedPage = getPageById(payload.metadata.activePageId)

            setupPageContentView()
        }
    }

    private fun setSlides(payload: KmeActiveContentModuleMessage.SetActiveContentPayload) {
        payload.metadata.slides?.let {
            rvSlides?.visibility = VISIBLE

            this.slides.clear()
            this.slides.addAll(it)
            this.slides.sortedBy { slide -> slide.slideNumber?.toInt() ?: 0 }

            selectedSlide = getSlideByNumber(config.currentSlide)

            setupSlideContentView()
            setupSlidesPreview()

            selectedSlide?.let { selectedSlide ->
                notifySlideSelected(selectedSlide)
            }
        }
    }


    private fun setupPageContentView() {
        originalImageSize = null

        selectedPage?.thumbnail?.let {
            if (it.isEmpty()) return

            val thumbnailParams = it.split(",")
            if (thumbnailParams.isNotEmpty()) {
                val decodedString: ByteArray = Base64.decode(thumbnailParams[1], Base64.DEFAULT)
                ivSlide.glide(decodedString) { originalSize ->
                    originalImageSize = Size(1280, 720)
                    setupWhiteboardView()
                }
            }
        }
    }

    private fun setupSlideContentView() {
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
                        val whiteboardConfig =
                            KmeWhiteboardView.Config(imageSize, imageBounds).apply {
                                cookie = config.cookie
                                fileUrl = config.fileUrl
                            }
                        init(whiteboardConfig)
                    }
                    ivSlide.viewTreeObserver.removeOnPreDrawListener(this)
                }
                return true
            }
        })
    }

    private fun setupSlidesPreview() {
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

    private fun getPageById(pageId: String?): Page? {
        return pages.find { page -> page.id == pageId }
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
                setupSlideContentView()
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
                setupSlideContentView()
                notifySlideSelected(slide)
            }
        }
    }

    override fun toSlide(slideNumber: Int) {
        val slide = getSlideByNumber(slideNumber)
        slide?.let { it ->
            selectedSlide = it
            setupSlideContentView()
            notifySlideSelected(slide)
        }
    }

    override fun init(whiteboardConfig: KmeWhiteboardView.Config?) {
        whiteboardLayout.init(whiteboardConfig)
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
        val payload: KmeActiveContentModuleMessage.SetActiveContentPayload,
        val cookie: String,
        val fileUrl: String,
    ) {
        var currentSlide: Int = 0
    }

}