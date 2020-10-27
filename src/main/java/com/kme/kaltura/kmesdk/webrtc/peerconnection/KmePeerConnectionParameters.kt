package com.kme.kaltura.kmesdk.webrtc.peerconnection

data class KmePeerConnectionParameters(
    val videoCodec: String = "VP8",
    val videoFlexfecEnabled: Boolean = false,
    val disableWebRtcAGCAndHPF: Boolean = false
)
