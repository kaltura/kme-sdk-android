package com.kme.kaltura.kmesdk.webrtc.peerconnection

import android.content.Context
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.VideoCapturer
import org.webrtc.VideoSink

interface IKmePublisherPeerConnection: IKmeBasePeerConnection {

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
     * @param context application context
     * @param localVideoSink local video sink
     * @param videoCapturer video capturer
     * @param useDataChannel indicates if data channel is used for speaking indication
     * @param iceServers collection of ice servers
     */
    fun createPeerConnection(
        context: Context,
        localVideoSink: VideoSink,
        videoCapturer: VideoCapturer?,
        useDataChannel: Boolean,
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
     * Setting remote SDP
     *
     * @param sdp [SessionDescription] object describes session description
     */
    fun setRemoteDescription(sdp: SessionDescription)

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

}