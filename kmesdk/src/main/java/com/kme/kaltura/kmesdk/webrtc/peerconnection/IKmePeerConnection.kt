package com.kme.kaltura.kmesdk.webrtc.peerconnection

import android.content.Context
import org.webrtc.*

/**
 * An interface for actions under WebRTC peer connection object
 */
interface IKmePeerConnection {

    /**
     * Creates peer connection factory
     *
     * @param context application context
     * @param events listener for events from [IKmePeerConnectionEvents]
     */
    fun createPeerConnectionFactory(
        context: Context,
        events: IKmePeerConnectionEvents
    )

    /**
     * Creates peer connection
     *
     * @param context application context
     * @param localVideoSink local video sink
     * @param remoteVideoSink remote video sink
     * @param videoCapturer video capturer
     * @param isPublisher indicates type of connection
     * @param iceServers collection of ice servers
     */
    fun createPeerConnection(
        context: Context,
        localVideoSink: VideoSink,
        remoteVideoSink: VideoSink,
        videoCapturer: VideoCapturer?,
        isPublisher: Boolean,
        iceServers: MutableList<PeerConnection.IceServer>
    )

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
