package com.kme.kaltura.kmesdk.webrtc.peerconnection

import android.content.Context
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.VideoCapturer
import org.webrtc.VideoSink

interface IKmeScreenSharerPeerConnection: IKmeBasePeerConnection {

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
     * Creates an offers
     */
    fun createOffer()

    /**
     * Setting remote SDP
     *
     * @param sdp [SessionDescription] object describes session description
     */
    fun setRemoteDescription(sdp: SessionDescription)

}