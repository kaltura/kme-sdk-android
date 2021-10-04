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
     * @param rendererView preview renderer
     */
    fun startPreview(
        videoCapturer: VideoCapturer?,
        rendererView: KmeSurfaceRendererView
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
     * @param videoCapturer video capturer
     * @param useDataChannel indicates if data channel is used for speaking indication
     * @param iceServers collection of ice servers
     */
    fun createPeerConnection(
        videoCapturer: VideoCapturer?,
        useDataChannel: Boolean,
        iceServers: MutableList<PeerConnection.IceServer>
    )

    /**
     * Attach renderer for peerconnection peer connection
     *
     * @param rendererView video renderer
     */
    fun setRenderer(rendererView: KmeSurfaceRendererView)

    /**
     * Remove connection renderer
     */
    fun removeRenderer()

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
    fun switchCamera(frontCamera: Boolean)

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
