package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.message.type.KmeSdpType

interface IKmeWebRTCController {

    fun createPeerConnection(
        localRenderer: KmeSurfaceRendererView?,
        remoteRenderer: KmeSurfaceRendererView?,
        turnUrl: String,
        turnUser: String,
        turnCred: String,
        listener: IKmePeerConnectionClientEvents
    )

    fun createOffer()

    fun setRemoteSdp(type: KmeSdpType, sdp: String)

    fun enableCamera(isEnable: Boolean)

    fun enableAudio(isEnable: Boolean)

    fun switchCamera()

    fun addRenderer()

    fun disconnectPeerConnection()

}
