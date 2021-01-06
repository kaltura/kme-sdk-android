package com.kme.kaltura.kmesdk.content.whiteboard

import android.graphics.RectF
import android.util.Size
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.WhiteboardPayload

interface IKmeWhiteboardLayout {

    fun init(originalImageSize: Size, imageBounds: RectF)

    fun applyDrawings(drawings: List<WhiteboardPayload.Drawing>)

}