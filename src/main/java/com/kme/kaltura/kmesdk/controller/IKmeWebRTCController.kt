package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView

interface IKmeWebRTCController {

    // Peer connections
    fun setTurnServer(
        turnUrl: String,
        turnUser: String,
        turnCred: String
    )

    fun addPublisherPeerConnection(
        userId: Long,
        renderer: KmeSurfaceRendererView,
        listener: IKmePeerConnectionClientEvents
    )

    fun addViewerPeerConnection(
        userId: Long,
        renderer: KmeSurfaceRendererView,
        listener: IKmePeerConnectionClientEvents
    )

    fun getPublisherConnection() : IKmePeerConnectionController

    fun getPeerConnection(userId: Long) : IKmePeerConnectionController?

    fun disconnectAllConnections()

    // Audio
    fun enableSpeakerphone(isEnable: Boolean)

}
