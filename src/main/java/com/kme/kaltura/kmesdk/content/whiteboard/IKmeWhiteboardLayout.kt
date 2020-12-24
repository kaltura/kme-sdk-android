package com.kme.kaltura.kmesdk.content.whiteboard

import android.util.Size
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.WhiteboardPayload

interface IKmeWhiteboardLayout {

    fun init(originalImageSize: Size)

    fun applyDrawings(drawings: List<WhiteboardPayload.Drawing>)

}