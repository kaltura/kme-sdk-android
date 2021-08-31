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

        setRenderer(rendererView)
    }

    override fun setRenderer(rendererView: KmeSurfaceRendererView) {
        removeRenderer()

        with(rendererView) {
            if (!isInitialized) {
                init(getRenderContext(), null)
                setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                setEnableHardwareScaler(true)
                setMirror(true)
            }
        }

        this.rendererView = rendererView
        localVideoTrack?.addSink(rendererView)
    }

    override fun removeRenderer() {
        this.rendererView?.let {
            localVideoTrack?.removeSink(it)
            it.release()
            this.rendererView = null
        }
    }

    /**
     * Toggle video
     */
    @RequiresPermission(Manifest.permission.CAMERA)
    override fun setVideoEnabled(enable: Boolean) {
        localVideoTrack?.setEnabled(enable)
    }

    /**
     * Switch between existing cameras
     */
    @RequiresPermission(Manifest.permission.CAMERA)
    override fun switchCamera(frontCamera: Boolean) {
        rendererView?.setMirror(frontCamera)
        videoCapturer?.let {
            if (it is CameraVideoCapturer) {
                it.switchCamera(null)
            }
        }
    }

}
