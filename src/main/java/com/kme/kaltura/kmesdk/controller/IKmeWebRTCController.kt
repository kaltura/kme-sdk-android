package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView

interface IKmeWebRTCController {

    fun setTurnServer(
        turnUrl: String,
        turnUser: String,
        turnCred: String
    )

    fun addPublisherPeerConnection(
        requestedUserIdStream: String,
        renderer: KmeSurfaceRendererView,
        listener: IKmePeerConnectionClientEvents
    ) : IKmePeerConnectionController?

    fun addViewerPeerConnection(
        requestedUserIdStream: String,
        renderer: KmeSurfaceRendererView,
        listener: IKmePeerConnectionClientEvents
    ) : IKmePeerConnectionController?

    fun getPublisherConnection() : IKmePeerConnectionController?

    fun getPeerConnection(requestedUserIdStream: String) : IKmePeerConnectionController?

    fun disconnectPeerConnection(requestedUserIdStream: String)

    fun disconnectAllConnections()

}
