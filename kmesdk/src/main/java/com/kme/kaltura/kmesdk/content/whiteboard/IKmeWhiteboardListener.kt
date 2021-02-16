package com.kme.kaltura.kmesdk.content.whiteboard

import android.graphics.PointF
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardBackgroundType


interface IKmeWhiteboardListener {

    fun init(whiteboardConfig: KmeWhiteboardView.Config?)

    fun updateLaserPosition(point: PointF)

    fun hideLaser()

    fun setDrawings(drawings: List<KmeWhiteboardModuleMessage.WhiteboardPayload.Drawing>)

    fun updateBackground(backgroundType: KmeWhiteboardBackgroundType?)

    fun addDrawing(drawing: KmeWhiteboardModuleMessage.WhiteboardPayload.Drawing)

    fun removeDrawing(layer: String)

    fun removeDrawings()

}