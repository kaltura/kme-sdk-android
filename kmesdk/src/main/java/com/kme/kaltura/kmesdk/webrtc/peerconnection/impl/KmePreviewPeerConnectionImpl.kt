package com.kme.kaltura.kmesdk.webrtc.peerconnection.impl

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import org.webrtc.CameraVideoCapturer
import org.webrtc.PeerConnection
import org.webrtc.RendererCommon
import org.webrtc.VideoCapturer

/**
 * An implementation actions under WebRTC peer connection object
 */
class KmePreviewPeerConnectionImpl(
    context: Context,
    events: IKmePeerConnectionEvents
) : KmeBasePeerConnectionImpl(context, events) {

    init {
        isPublisher = false
        isScreenShare = false
    }

    /**
     * Creates a local video preview
     */
    override fun startPreview(
        videoCapturer: VideoCapturer?,
        rendererView: KmeSurfaceRendererView
    ) {
        this.videoCapturer = videoCapturer

        peerConnection =
            factory?.createPeerConnection(PeerConnection.RTCConfiguration(listOf()), pcObserver)
        peerConnection?.let { peerConnection ->
            videoCapturer?.let {
                peerConnection.addTrack(createLocalVideoTrack(videoCapturer), videoStreamId)
            }
        }

        addRenderer(rendererView)
    }

    /**
     * Add renderer for peer connection
     */
    override fun addRenderer(rendererView: KmeSurfaceRendererView) {
        with(rendererView) {
            if (!isInitialized) {
                init(getRenderContext(), null)
                setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                setEnableHardwareScaler(true)
                setMirror(true)
            }

            renderers.add(this)
            localVideoTrack?.addSink(this)
        }
    }

    /**
     * Remove specific renderer for peer connection
     */
    override fun removeRenderer(rendererView: KmeSurfaceRendererView) {
        renderers.find {
            it == rendererView
        }?.let {
            localVideoTrack?.removeSink(it)
            it.release()
            renderers.remove(it)
        }
    }

    /**
     * Remove all connection renderers
     */
    override fun removeRenderers() {
        renderers.forEach {
            localVideoTrack?.removeSink(it)
            it.release()
        }
        renderers.clear()
    }

    /**
     * Toggle video
     */
    @RequiresPermission(Manifest.permission.CAMERA)
    override fun setVideoEnabled(enable: Boolean) {
        enableVideoSource(enable)
//        localVideoTrack?.setEnabled(enable)
    }

    /**
     * Enable/Disable outgoing video stream
     */
    override fun enableVideoSource(enable: Boolean) {
        if (enable) {
            if (!videoCapturerEnabled) {
                videoCapturer?.startCapture(
                    VIDEO_WIDTH,
                    VIDEO_HEIGHT,
                    VIDEO_FPS
                )
                videoCapturerEnabled = true
            }
        } else if (videoCapturerEnabled) {
            try {
                videoCapturer?.stopCapture()
            } catch (e: InterruptedException) {
            }
            videoCapturerEnabled = false
        }
    }

    /**
     * Switch between existing cameras
     */
    @RequiresPermission(Manifest.permission.CAMERA)
    override fun switchCamera(frontCamera: Boolean) {
        renderers.forEach { rendererView ->
            rendererView.setMirror(frontCamera)
        }

        videoCapturer?.let {
            if (it is CameraVideoCapturer) {
                it.switchCamera(null)
            }
        }
    }

}
