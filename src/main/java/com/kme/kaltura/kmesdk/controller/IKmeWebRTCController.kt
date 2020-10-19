package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView

interface IKmeWebRTCController {

    fun createPeerConnection(
        localRenderer: KmeSurfaceRendererView,
        remoteRenderer: KmeSurfaceRendererView,
        listener: IKmePeerConnectionClientEvents
    )

    fun createOffer()

    fun createAnswer()

    fun enableCamera(isEnable: Boolean)

    fun enableAudio(isEnable: Boolean)

    fun switchCamera()

    fun addRenderer()

    fun disconnectPeerConnection()

}
