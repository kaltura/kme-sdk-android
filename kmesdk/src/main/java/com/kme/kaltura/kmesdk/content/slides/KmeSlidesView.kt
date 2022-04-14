package com.kme.kaltura.kmesdk.content.slides

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Size
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.content.whiteboard.IKmeWhiteboardChangeListener
import com.kme.kaltura.kmesdk.content.whiteboard.KmeWhiteboardView
import com.kme.kaltura.kmesdk.databinding.LayoutSlidesViewBinding
import com.kme.kaltura.kmesdk.glide
import com.kme.kaltura.kmesdk.glideAsGif
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.ActiveContentPayload.Page
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.ActiveContentPayload.Slide
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.WhiteboardPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType
import com.kme.kaltura.kmesdk.ws.message.type.KmeFileType
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardBackgroundType

/**
 * An implementation of slides view in the room
 */
class KmeSlidesView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), IKmeSlidesListener {

    private lateinit var config: Config

    private var binding: LayoutSlidesViewBinding =
        LayoutSlidesViewBinding.inflate(LayoutInflater.from(context), this)

    private var slides: MutableList<Slide> = mutableListOf()
    private var slidesAdapter: SlidesAdapter? = null
    private var selectedSlide: Slide? = null

    private var pages: MutableList<Page> = mutableListOf()
    private var selectedPage: Page? = null

    private var originalImageSize: Size? = null

    /**
     * Initialize function. Setting config
     */
    override fun init(config: Config) {
        this.config = config

        with(binding) {

            whiteboardLayout.changeListener = object : IKmeWhiteboardChangeListener {
                override fun onChanged() {
//                    val bitmapFromView = whiteboardContainer.getBitmapFromView()
//                    zoomLayout.setImageBitmap(bitmapFromView)

//                    zoomLayout.setImageDrawable(binding.ivSlide.drawable)
                }
            }
        }

        if (KmeContentType.SLIDES == config.payload.contentType) {
            setSlides(config.payload)
        } else if (KmeContentType.WHITEBOARD == config.payload.contentType) {
            setWhiteboardPages(config.payload)
        }
    }

    private fun setWhiteboardPages(payload: KmeActiveContentModuleMessage.SetActiveContentPayload) {
        payload.metadata.pages?.let {
            binding.rvSlides.visibility = GONE

            this.pages.clear()
            this.pages.addAll(it)

            setActivePage(payload.metadata.activePageId)
        }
    }

    /**
     * Set active page from board list
     */
    override fun setActivePage(activePageId: String?) {
        selectedPage = getPageById(activePageId)
        if (selectedPage == null) {
            val newPage = Page(activePageId).apply {
                backgroundMetadata = KmeWhiteboardBackgroundType.BLANK
            }
            selectedPage = newPage
            this.pages.add(newPage)
        }
        setupPageContentView()
    }

    /**
     * Set actual slides
     */
    private fun setSlides(payload: KmeActiveContentModuleMessage.SetActiveContentPayload) {
        payload.metadata.slides?.let {
            binding.rvSlides.visibility = VISIBLE

            this.selectedPage = null
            this.slides.clear()
            this.slides.addAll(it)
            this.slides.sortedBy { slide -> slide.slideNumber?.toInt() ?: 0 }

            selectedSlide = getSlideByNumber(config.currentSlide)

            if (payload.metadata.fileType == KmeFileType.GIF) {
                setupGifSlideContentView()
            } else {
                setupSlideContentView()
            }

            setupSlidesPreview()

            selectedSlide?.let { selectedSlide ->
                notifySlideSelected(selectedSlide)
            }
        }
    }

    private fun setupPageContentView() {
        originalImageSize = null

        binding.ivSlide.glide(R.drawable.bg_blank_whiteboard) {
            originalImageSize = Size(1280, 720)
            setupWhiteboardView()
        }
    }

    private fun setupSlideContentView() {
        originalImageSize = null

        selectedSlide?.let {
            binding.ivSlide.glide(it.url, config.cookie, config.fileUrl) { originalSize ->
                originalImageSize = originalSize
                setupWhiteboardView()
            }
        }
    }

    private fun setupGifSlideContentView() {
        originalImageSize = null

        selectedSlide?.let {
            binding.ivSlide.glideAsGif(it.url, config.cookie, config.fileUrl) { originalSize ->
                originalImageSize = originalSize
                setupWhiteboardView()
            }
        }
    }

    private fun setupWhiteboardView() {
        with(binding) {
            zoomLayout.doOnPreDraw {
                val imageBounds = getSlideDrawableBounds()
                if (imageBounds != null && !imageBounds.isEmpty) {
                    originalImageSize?.let { imageSize ->
                        val whiteboardConfig =
                            KmeWhiteboardView.Config(imageSize, imageBounds).apply {
                                cookie = config.cookie
                                fileUrl = config.fileUrl
                                backgroundType =
                                    if (KmeContentType.WHITEBOARD == config.payload.contentType) {
                                        selectedPage?.backgroundMetadata
                                    } else {
                                        null
                                    }
                            }
                        init(whiteboardConfig)
                    }
                }
            }
        }

        setupWhiteboardSizeChangeListener()
    }

    private fun setupWhiteboardSizeChangeListener() {
        with(binding) {
            whiteboardContainer.addOnLayoutChangeListener { v, _, _, _, _, oldLeft, oldTop, oldRight, oldBottom ->
                val oldWidth: Int = oldRight - oldLeft
                val oldHeight: Int = oldBottom - oldTop

                if (v.width != oldWidth || v.height != oldHeight) {
                    val imageBounds = getSlideDrawableBounds()
                    if (imageBounds != null) {
                        onImageBoundsChanged(imageBounds)
                    }
                }
            }
        }
    }

    private fun getSlideDrawableBounds(): RectF? {
        with(binding) {
            val drawable = ivSlide.drawable
            if (drawable != null) {
                val imageBounds = RectF()
                ivSlide.imageMatrix.mapRect(imageBounds, RectF(drawable.bounds))
                return imageBounds
            }
        }

        return null
    }

    private fun setupSlidesPreview() {
        with(binding) {
            if (config.showPreview) showPreview() else hidePreview()
            slidesAdapter = SlidesAdapter(config.cookie, config.fileUrl).apply {
                setData(slides)
            }
            rvSlides.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = slidesAdapter
            }
        }
    }

    private fun notifySlideSelected(selectedSlide: Slide) {
        val indexOf = slides.indexOf(selectedSlide)
        if (indexOf >= 0) {
            slides.forEachIndexed { index, slide ->
                slide.isSelected = index == indexOf
            }
            slidesAdapter?.setData(slides)

            with(binding) {
                rvSlides.post {
                    (rvSlides.layoutManager as LinearLayoutManager?)
                        ?.scrollToPositionWithOffset(indexOf, 0)
                }
            }
        }
    }

    private fun getSlideByNumber(number: Int): Slide? {
        return slides.find { slide -> slide.slideNumber?.toInt() == number }
    }

    private fun getPageById(pageId: String?): Page? {
        return pages.find { page -> page.id == pageId }
    }

    /**
     * Getting actual slide
     */
    override val currentSlide: Slide?
        get() = selectedSlide

    /**
     * Getting size of slides collection
     */
    override val size: Int
        get() = slides.size

    /**
     * Asking for the next slide form slides collection
     */
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

    /**
     * Asking for the previous slide form slides collection
     */
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

    /**
     * Getting slide by position from slides collection
     */
    override fun toSlide(slideNumber: Int) {
        val slide = getSlideByNumber(slideNumber)
        slide?.let { it ->
            selectedSlide = it
            setupSlideContentView()
            notifySlideSelected(slide)
        }
    }

    override fun enableAnnotations(enable: Boolean) {
        binding.whiteboardLayout.enableAnnotations(enable)
    }

    /**
     * Initialize function. Setting config.
     */
    override fun init(whiteboardConfig: KmeWhiteboardView.Config?) {
        binding.whiteboardLayout.init(whiteboardConfig)
        enableAnnotations(config.isAnnotationsEnabled)
    }

    override fun onImageBoundsChanged(imageBounds: RectF) {
        binding.whiteboardLayout.onImageBoundsChanged(imageBounds)
    }

    /**
     * Sets the list of drawings.
     */
    override fun setDrawings(drawings: List<WhiteboardPayload.Drawing>) {
        binding.whiteboardLayout.setDrawings(drawings)

        selectedPage?.let {
            updateBackground(it.backgroundMetadata)
        }
    }

    /**
     * Adds the drawing.
     */
    override fun addDrawing(drawing: WhiteboardPayload.Drawing) {
        binding.whiteboardLayout.addDrawing(drawing)
    }

    /**
     * Updates the current position of the laser pointer. Show pointer if not already created.
     */
    override fun updateLaserPosition(point: PointF) {
        binding.whiteboardLayout.updateLaserPosition(point)
    }

    /**
     * Hide the laser pointer.
     */
    override fun hideLaser() {
        binding.whiteboardLayout.hideLaser()
    }

    /**
     * Updates the whiteboard background.
     */
    override fun updateBackground(backgroundType: KmeWhiteboardBackgroundType?) {
        selectedPage?.let {
            val index = pages.indexOf(it)
            if (index >= 0) {
                it.backgroundMetadata = backgroundType
                pages[index] = it
            }
        }
        binding.whiteboardLayout.updateBackground(backgroundType)
    }

    /**
     * Removes the existing drawing from the whiteboard view.
     */
    override fun removeDrawing(layer: String) {
        binding.whiteboardLayout.removeDrawing(layer)
    }

    /**
     * Removes all drawings from the whiteboard view.
     */
    override fun removeDrawings() {
        binding.whiteboardLayout.removeDrawings()
    }

    /**
     * Show a preview list of current slides
     */
    override fun showPreview() {
        if (!config.showPreview || this.pages.isNotEmpty()) {
            return
        }
        binding.rvSlides.visibility = VISIBLE
    }

    /**
     * Hide a preview list of current slides
     */
    override fun hidePreview() {
        if (this.pages.isNotEmpty()) {
            return
        }
        binding.rvSlides.visibility = GONE
    }

    override fun setZoomEnabled(zoomEnabled: Boolean) {
        binding.zoomLayout.isEnabled = zoomEnabled
    }

    class Config(
        val payload: KmeActiveContentModuleMessage.SetActiveContentPayload,
        val cookie: String,
        val fileUrl: String,
    ) {
        var isAnnotationsEnabled: Boolean = true
        var currentSlide: Int = 0
        var showPreview: Boolean = true
    }

}