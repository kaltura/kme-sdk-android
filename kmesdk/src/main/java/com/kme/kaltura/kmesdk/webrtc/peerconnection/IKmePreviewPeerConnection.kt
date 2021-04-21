package com.kme.kaltura.kmesdk.webrtc.peerconnection

import android.content.Context
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import org.webrtc.PeerConnection
import org.webrtc.VideoCapturer
import org.webrtc.VideoSink

interface IKmePreviewPeerConnection: IKmeBasePeerConnection {

    /**
     * Creates a local video preview
     *
     * @param context application context
     * @param videoCapturer video capturer
     * @param previewRenderer preview renderer
     */
    fun startPreview(
        context: Context,
        videoCapturer: VideoCapturer?,
        previewRenderer: KmeSurfaceRendererView
    )

    /**
     * Creates peer connection
     *
     * @param context application context
     * @param localVideoSink local video sink
     * @param videoCapturer video capturer
     * @param iceServers collection of ice servers
     */
    fun createPeerConnection(
        context: Context,
        localVideoSink: VideoSink,
        videoCapturer: VideoCapturer?,
        iceServers: MutableList<PeerConnection.IceServer>
    )

    /**
     * Toggle video
     *
     * @param enable flag to enable/disable video
     */
    fun setVideoEnabled(enable: Boolean)

    /**
     * Switch between existing cameras
     */
    fun switchCamera()

}