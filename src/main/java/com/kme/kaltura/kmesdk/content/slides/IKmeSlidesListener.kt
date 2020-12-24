package com.kme.kaltura.kmesdk.content.slides

import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage

interface IKmeSlidesListener {

    fun init(config: KmeSlidesView.Config)

    fun setSlides(slides: List<KmeActiveContentModuleMessage.ActiveContentPayload.Slide>)

    val currentSlide: KmeActiveContentModuleMessage.ActiveContentPayload.Slide?

    val size: Int

    fun next()

    fun previous()

    fun toSlide(slideNumber: Int)

    fun applyDrawings(drawings: List<KmeWhiteboardModuleMessage.WhiteboardPayload.Drawing>)

}