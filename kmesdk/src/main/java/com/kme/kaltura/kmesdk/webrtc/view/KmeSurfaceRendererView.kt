package com.kme.kaltura.kmesdk.webrtc.view

import android.content.Context
import android.util.AttributeSet
import org.webrtc.EglBase
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer

/**
 * Kme wrapper under [org.webrtc.SurfaceViewRenderer] class
 */
class KmeSurfaceRendererView(context: Context?, attrs: AttributeSet?) :
    SurfaceViewRenderer(context, attrs) {

    var isInitialized = false

    override fun init(
        sharedContext: EglBase.Context?,
        rendererEvents: RendererCommon.RendererEvents?
    ) {
        if (!isInitialized) {
            isInitialized = true
            super.init(sharedContext, rendererEvents)
        }
    }

    override fun release() {
        super.release()
        isInitialized = false
    }

}