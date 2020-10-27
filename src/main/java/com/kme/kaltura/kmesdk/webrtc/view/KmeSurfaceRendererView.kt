package com.kme.kaltura.kmesdk.webrtc.view

import android.content.Context
import android.util.AttributeSet
import org.webrtc.SurfaceViewRenderer

class KmeSurfaceRendererView(context: Context?, attrs: AttributeSet?) :
    SurfaceViewRenderer(context, attrs) {
    var userId: Long = 0L
}
