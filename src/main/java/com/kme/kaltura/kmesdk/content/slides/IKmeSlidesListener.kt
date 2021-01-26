package com.kme.kaltura.kmesdk.content.slides

import com.kme.kaltura.kmesdk.content.whiteboard.IKmeWhiteboardListener
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage

interface IKmeSlidesListener : IKmeWhiteboardListener {

    fun init(config: KmeSlidesView.Config)

    val currentSlide: KmeActiveContentModuleMessage.ActiveContentPayload.Slide?

    val size: Int

    fun next()

    fun previous()

    fun toSlide(slideNumber: Int)

}