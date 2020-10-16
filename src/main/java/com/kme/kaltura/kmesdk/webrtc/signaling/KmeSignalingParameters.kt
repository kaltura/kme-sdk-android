package com.kme.kaltura.kmesdk.webrtc.signaling

import org.webrtc.IceCandidate
import org.webrtc.PeerConnection.IceServer
import org.webrtc.SessionDescription

data class KmeSignalingParameters(
    val iceServers: MutableList<IceServer> = mutableListOf(),
    val initiator: Boolean? = true,
    val clientId: String? = "1234",
    val wssUrl: String? = null,
    val wssPostUrl: String? = null,
    val offerSdp: SessionDescription? = null,
    val iceCandidates: List<IceCandidate?>? = null
)
