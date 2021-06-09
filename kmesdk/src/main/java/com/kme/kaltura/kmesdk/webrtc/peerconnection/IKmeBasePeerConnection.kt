package com.kme.kaltura.kmesdk.webrtc.peerconnection

import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import org.webrtc.*

/**
 * An interface for actions under WebRTC peer connection object
 */
interface IKmeBasePeerConnection {

    /**
    * Creates a local video preview
    *
    * @param videoCapturer video capturer
    * @param previewRenderer preview renderer
    */
    fun startPreview(
        videoCapturer: VideoCapturer?,
        previewRenderer: KmeSurfaceRendererView
    )

    /**
     * Set preferred settings for establish p2p connection
     *
     * @param preferredMicEnabled flag for enable/disable micro
     * @param preferredCamEnabled flag for enable/disable camera
     */
    fun setPreferredSettings(
        preferredMicEnabled: Boolean,
        preferredCamEnabled: Boolean
    )

    /**
     * Creates peer connection
     *
     * @param localVideoSink local video sink
     * @param remoteVideoSink remote video sink
     * @param videoCapturer video capturer
     * @param useDataChannel indicates if data channel is used for speaking indication
     * @param iceServers collection of ice servers
     */
    fun createPeerConnection(
        localVideoSink: VideoSink,
        remoteVideoSink: VideoSink,
        videoCapturer: VideoCapturer?,
        useDataChannel: Boolean,
        iceServers: MutableList<PeerConnection.IceServer>
    )

    /**
     * Replace renderer for publisher connection
     */
    fun changeLocalRenderer(videoSink: VideoSink)

    /**
     * Replace renderer for viewer connection
     */
    fun changeRemoteRenderer(videoSink: VideoSink)

    /**
     * Toggle audio
     *
     * @param enable flag to enable/disable audio
     */
    fun setAudioEnabled(enable: Boolean)

    /**
     * Toggle video
     *
     * @param enable flag to enable/disable video
     */
    fun setVideoEnabled(enable: Boolean)

    /**
     * Creates an offers
     */
    fun createOffer()

    /**
     * Creates an answer
     */
    fun createAnswer()

    /**
     * Setting remote SDP
     *
     * @param sdp [SessionDescription] object describes session description
     */
    fun setRemoteDescription(sdp: SessionDescription)

    /**
     * Handle adding ICE candidates
     *
     * @param candidate ICE candidate to add
     */
    fun addRemoteIceCandidate(candidate: IceCandidate?)

    /**
     * Handle remove remote ICE candidates
     *
     * @param candidates ICE candidates to remove
     */
    fun removeRemoteIceCandidates(candidates: Array<IceCandidate>)

    /**
     * Disable outgoing video stream
     */
    fun stopVideoSource()

    /**
     * Enable outgoing video stream
     */
    fun startVideoSource()

    /**
     * Switch between existing cameras
     */
    fun switchCamera()

    /**
     * Closes actual connection
     */
    fun close()

    /**
     * Getting rendering context for WebRTC
     *
     * @return rendering context
     */
    fun getRenderContext(): EglBase.Context?

}
