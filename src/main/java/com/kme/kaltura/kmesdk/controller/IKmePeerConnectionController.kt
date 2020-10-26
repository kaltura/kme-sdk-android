package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.message.type.KmeSdpType

interface IKmePeerConnectionController {

    fun setTurnServer(
        turnUrl: String,
        turnUser: String,
        turnCred: String
    )

    fun setLocalRenderer(localRenderer: KmeSurfaceRendererView)

    fun setRemoteRenderer(remoteRenderer: KmeSurfaceRendererView)

    fun createPeerConnection(
        isPublisher: Boolean,
        userId: Long,
        listener: IKmePeerConnectionClientEvents
    )

    fun createPeerConnection(
        isPublisher: Boolean,
        userId: Long,
        mediaServerId: Long,
        listener: IKmePeerConnectionClientEvents
    )

    fun setMediaServerId(mediaServerId: Long)

    fun createOffer()

    fun setRemoteSdp(type: KmeSdpType, sdp: String)

    fun enableCamera(isEnable: Boolean)

    fun enableAudio(isEnable: Boolean)

    fun switchCamera()

    fun disconnectPeerConnection()

}
