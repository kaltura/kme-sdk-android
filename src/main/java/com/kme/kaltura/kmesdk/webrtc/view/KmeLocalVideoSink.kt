package com.kme.kaltura.kmesdk.webrtc.view

import org.webrtc.VideoFrame
import org.webrtc.VideoSink

class KmeLocalVideoSink : VideoSink {

    private var target: VideoSink? = null

    @Synchronized
    override fun onFrame(frame: VideoFrame) {
        target?.onFrame(frame)
    }

    @Synchronized
    fun setTarget(target: VideoSink?) {
        this.target = target
    }

}
