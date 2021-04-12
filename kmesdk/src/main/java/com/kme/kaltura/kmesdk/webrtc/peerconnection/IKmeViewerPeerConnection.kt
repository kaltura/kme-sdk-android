package com.kme.kaltura.kmesdk.webrtc.peerconnection

import android.content.Context
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.VideoSink

interface IKmeViewerPeerConnection: IKmeBasePeerConnection {

    /**
     * Creates peer connection
     *
     * @param context application context
     * @param remoteVideoSink remote video sink
     * @param useDataChannel indicates if data channel is used for speaking indication
     * @param iceServers collection of ice servers
     */
    fun createPeerConnection(
        context: Context,
        remoteVideoSink: VideoSink,
        useDataChannel: Boolean,
        iceServers: MutableList<PeerConnection.IceServer>
    )

    /**
     * Setting remote SDP
     *
     * @param sdp [SessionDescription] object describes session description
     */
    fun setRemoteDescription(sdp: SessionDescription)

    /**
     * Creates an answer
     */
    fun createAnswer()

}