package com.kme.kaltura.kmesdk.webrtc.peerconnection

data class KmePeerConnectionParameters(
    val videoCallEnabled: Boolean = true,
    val loopback: Boolean = false,
    val tracing: Boolean = false,
    val videoMaxBitrate: Int = 1700,
    val videoCodec: String = "VP8",
    val videoCodecHwAcceleration: Boolean = true,
    val videoFlexfecEnabled: Boolean = false,
    val audioStartBitrate: Int = 32,
    val audioCodec: String = "OPUS",
    val noAudioProcessing: Boolean = false,
    val aecDump: Boolean = false,
    val useOpenSLES: Boolean = false,
    val disableBuiltInAEC: Boolean = false,
    val disableBuiltInAGC: Boolean = false,
    val disableBuiltInNS: Boolean = false,
    val enableLevelControl: Boolean = false,
    val disableWebRtcAGCAndHPF: Boolean = false,
    val dataChannelParameters: DataChannelParameters? = null
)
