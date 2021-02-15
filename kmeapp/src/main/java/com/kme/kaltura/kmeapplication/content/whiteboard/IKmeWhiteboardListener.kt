package com.kme.kaltura.kmesdk.content.whiteboard

import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.WhiteboardPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardBackgroundType

interface IKmeWhiteboardListener {

    fun init(whiteboardConfig: KmeWhiteboardView.Config?)

    fun setDrawings(drawings: List<WhiteboardPayload.Drawing>)

    fun updateBackground(backgroundType: KmeWhiteboardBackgroundType?)

    fun addDrawing(drawing: WhiteboardPayload.Drawing)

    fun removeDrawing(layer: String)

    fun removeDrawings()

}