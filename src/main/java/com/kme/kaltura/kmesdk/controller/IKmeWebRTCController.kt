package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceViewRenderer

interface IKmeWebRTCController {

    fun createPeerConnection(renderer: KmeSurfaceViewRenderer, listener: IKmePeerConnectionClientEvents)

    fun createOffer()

    fun createAnswer()

    fun enableCamera(isEnable: Boolean)

    fun enableAudio(isEnable: Boolean)

    fun switchCamera()

    fun addRenderer()

    fun disconnectPeerConnection()

}
