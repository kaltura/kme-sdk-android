package com.kme.kaltura.kmesdk.webrtc.signaling

import org.webrtc.IceCandidate
import org.webrtc.PeerConnection.IceServer

data class KmeSignalingParameters(
    var iceServers: MutableList<IceServer> = mutableListOf(),
    var isPublisher: Boolean = true,
    var iceCandidates: List<IceCandidate?>? = null
)
