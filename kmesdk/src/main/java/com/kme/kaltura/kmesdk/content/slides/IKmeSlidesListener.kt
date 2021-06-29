package com.kme.kaltura.kmesdk.content.slides

import com.kme.kaltura.kmesdk.content.whiteboard.IKmeWhiteboardListener
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage

/**
 * An interface for slides in the room
 */
interface IKmeSlidesListener : IKmeWhiteboardListener {

    /**
     * Initialize function. Setting config
     *
     * @param config sets metadata for slides
     */
    fun init(config: KmeSlidesView.Config)

    /**
     * Getting actual slide
     */
    val currentSlide: KmeActiveContentModuleMessage.ActiveContentPayload.Slide?

    /**
     * Getting size of slides collection
     */
    val size: Int

    /**
     * Asking for the next slide form slides collection
     */
    fun next()

    /**
     * Asking for the previous slide form slides collection
     */
    fun previous()

    /**
     * Getting slide by position from slides collection
     *
     * @param slideNumber position of a slide
     */
    fun toSlide(slideNumber: Int)

    /**
     * Show a preview list of current slides
     */
    fun showPreview()

    /**
     * Hide a preview list of current slides
     */
    fun hidePreview()

    /**
     * Set active page from board list
     *
     * @param activePageId id of the active page
     */
    fun setActivePage(activePageId: String?)

    /**
     * Enable/disable zoom
     *
     * @param zoomEnabled true if zoom is enable
     */
    fun setZoomEnabled(zoomEnabled: Boolean)

}
