package com.kme.kaltura.kmesdk.webrtc.signaling

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

interface IKmeSignalingEvents {

    /**
     * Callback fired once remote SDP is received.
     */
    fun onRemoteDescription(sdp: SessionDescription)

    /**
     * Callback fired once remote Ice candidate is received.
     */
    fun onRemoteIceCandidate(candidate: IceCandidate)

    /**
     * Callback fired once remote Ice candidate removals are received.
     */
    fun onRemoteIceCandidatesRemoved(candidates: Array<IceCandidate>)

    /**
     * Callback fired once channel is closed.
     */
    fun onChannelClose()

    /**
     * Callback fired once channel error happened.
     */
    fun onChannelError(description: String)

}
