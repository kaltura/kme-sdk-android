package com.kme.kaltura.kmesdk.content.whiteboard

import android.graphics.RectF
import android.util.Size
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.WhiteboardPayload

interface IKmeWhiteboardListener {

    fun init(originalImageSize: Size, imageBounds: RectF)

    fun setDrawings(drawings: List<WhiteboardPayload.Drawing>)

    fun addDrawing(drawing: WhiteboardPayload.Drawing)

    fun removeDrawing(layer: String)

    fun removeDrawings()

}