package com.kme.kaltura.kmesdk.content.slides

import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage

/**
 * An interface for slides in the room
 */
interface IKmeSlidesListener {

    /**
     * Initialize function. Setting config
     *
     * @param config sets metadata for slides
     */
    fun init(config: KmeSlidesView.Config)

    /**
     * Set actual slides
     *
     * @param slides room slides to set
     */
    fun setSlides(slides: List<KmeActiveContentModuleMessage.ActiveContentPayload.Slide>)

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

}
