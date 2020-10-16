package com.kme.kaltura.kmesdk.webrtc.view

import org.webrtc.VideoRenderer
import org.webrtc.VideoRenderer.I420Frame

class KmeProxyRenderer : VideoRenderer.Callbacks {

    private var target: VideoRenderer.Callbacks? = null

    @Synchronized
    override fun renderFrame(frame: I420Frame) {
        if (target == null) {
            VideoRenderer.renderFrameDone(frame)
            return
        }
        target?.renderFrame(frame)
    }

    @Synchronized
    fun setTarget(target: VideoRenderer.Callbacks?) {
        this.target = target
    }

}
