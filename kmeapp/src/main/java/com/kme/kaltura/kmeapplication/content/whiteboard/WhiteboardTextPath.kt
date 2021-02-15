package com.kme.kaltura.kmesdk.content.whiteboard

import android.graphics.Path

data class WhiteboardTextPath(
    val text: String,
    val path: Path,
    val vOffset: Float,
    val hOffset: Float
)