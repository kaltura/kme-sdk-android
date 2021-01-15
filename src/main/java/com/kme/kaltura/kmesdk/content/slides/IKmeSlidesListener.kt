package com.kme.kaltura.kmesdk.content.slides

import com.kme.kaltura.kmesdk.content.whiteboard.IKmeWhiteboardListener
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage

interface IKmeSlidesListener : IKmeWhiteboardListener{

    fun init(config: KmeSlidesView.Config)

    fun setSlides(slides: List<KmeActiveContentModuleMessage.ActiveContentPayload.Slide>)

    val currentSlide: KmeActiveContentModuleMessage.ActiveContentPayload.Slide?

    val size: Int

    fun next()

    fun previous()

    fun toSlide(slideNumber: Int)

}