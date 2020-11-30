package com.kme.kaltura.kmesdk.webrtc.peerconnection

import android.content.Context
import org.webrtc.*

interface IKmePeerConnection {

    fun createPeerConnectionFactory(
        context: Context,
        events: IKmePeerConnectionEvents
    )

    fun createPeerConnection(
        context: Context,
        localVideoSink: VideoSink,
        remoteVideoSink: VideoSink,
        videoCapturer: VideoCapturer?,
        isPublisher: Boolean,
        iceServers: MutableList<PeerConnection.IceServer>
    )

    fun setAudioEnabled(enable: Boolean)

    fun setVideoEnabled(enable: Boolean)

    fun createOffer()

    fun createAnswer()

    fun setRemoteDescription(sdp: SessionDescription)

    fun addRemoteIceCandidate(candidate: IceCandidate?)

    fun removeRemoteIceCandidates(candidates: Array<IceCandidate>)

    fun stopVideoSource()

    fun startVideoSource()

    fun switchCamera()

    fun close()

    fun getRenderContext(): EglBase.Context?

}