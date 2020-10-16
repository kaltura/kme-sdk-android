package com.kme.kaltura.kmesdk.controller.impl

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.kme.kaltura.kmesdk.controller.IKmeWebRTCController
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionEvents
import com.kme.kaltura.kmesdk.webrtc.peerconnection.KmePeerConnectionClient
import com.kme.kaltura.kmesdk.webrtc.peerconnection.KmePeerConnectionParameters
import com.kme.kaltura.kmesdk.webrtc.signaling.IKmeSignalingEvents
import com.kme.kaltura.kmesdk.webrtc.signaling.KmeSignalingParameters
import com.kme.kaltura.kmesdk.webrtc.view.KmeProxyVideoSink
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceViewRenderer
import org.webrtc.*
import org.webrtc.RendererCommon.ScalingType
import java.util.*

class KmeWebRTCControllerImpl(
    private val context: Context,
    private val gson: Gson
) : IKmeWebRTCController, IKmeSignalingEvents, IKmePeerConnectionEvents {

    private val TAG = KmeWebRTCControllerImpl::class.java.canonicalName

    private var peerConnectionClient: KmePeerConnectionClient? = null
    private lateinit var peerConnectionParameters: KmePeerConnectionParameters
    private lateinit var signalingParameters: KmeSignalingParameters
    private lateinit var listener: IKmePeerConnectionClientEvents

//    private val remoteProxyRenderer: KmeProxyRenderer = KmeProxyRenderer()
    private val localProxyVideoSink: KmeProxyVideoSink = KmeProxyVideoSink()
    private var localRenderer: KmeSurfaceViewRenderer? = null
    private val remoteRenderers: List<VideoRenderer.Callbacks> = ArrayList()

    init {
//        remoteRenderers.toMutableList().add(remoteProxyRenderer)
    }

    // Public PeerConnection API
    override fun createPeerConnection(
        renderer: KmeSurfaceViewRenderer,
        listener: IKmePeerConnectionClientEvents
    ) {
        Log.d(TAG, "createPeerConnection")

        peerConnectionClient = KmePeerConnectionClient()

        localRenderer = renderer
        localRenderer?.init(peerConnectionClient?.getRenderContext(), null)
        localRenderer?.setScalingType(ScalingType.SCALE_ASPECT_FILL)
        localRenderer?.setEnableHardwareScaler(true)
        localRenderer?.setMirror(true)

        localProxyVideoSink.setTarget(localRenderer)

        this.listener = listener

        peerConnectionParameters = KmePeerConnectionParameters()

        peerConnectionClient?.createPeerConnectionFactory(
            context,
            peerConnectionParameters,
            this
        )

        signalingParameters = KmeSignalingParameters()

        var videoCapturer: VideoCapturer? = null
        if (peerConnectionParameters.videoCallEnabled) {
            videoCapturer = createCameraCapturer(context)
        }
        peerConnectionClient?.createPeerConnection(
            localProxyVideoSink,
            remoteRenderers,
            videoCapturer,
            signalingParameters
        )
    }

    override fun createOffer() {
        Log.d(TAG, "createOffer")
        if (signalingParameters.initiator!!) {
            peerConnectionClient?.createOffer()
        }
    }

    override fun createAnswer() {
        Log.d(TAG, "createAnswer")
        if (signalingParameters.offerSdp != null) {
            peerConnectionClient?.setRemoteDescription(signalingParameters.offerSdp!!)
            peerConnectionClient?.createAnswer()
        }
        if (signalingParameters.iceCandidates != null) {
            for (iceCandidate in signalingParameters.iceCandidates!!) {
                peerConnectionClient?.addRemoteIceCandidate(iceCandidate)
            }
        }
    }

    override fun enableCamera(isEnable: Boolean) {
        Log.d(TAG, "enableCamera $isEnable")
        peerConnectionClient?.setVideoEnabled(isEnable)
    }

    override fun enableAudio(isEnable: Boolean) {
        Log.d(TAG, "enableAudio $isEnable")
        peerConnectionClient?.setAudioEnabled(isEnable)
    }

    override fun switchCamera() {
        Log.d(TAG, "switchCamera")
        peerConnectionClient?.switchCamera()
    }

    override fun addRenderer() {
        // TODO: not sure need this here. Maybe should be a part of client
    }

    override fun disconnectPeerConnection() {
//        remoteProxyRenderer.setTarget(null)
        localProxyVideoSink.setTarget(null)

        // TODO: not sure need this here. Maybe should be a part of client
        if (localRenderer != null) {
            localRenderer!!.release()
            localRenderer = null
        }

        peerConnectionClient?.close()
        peerConnectionClient = null
    }

    // Events coming from server via WS connection
    override fun onRemoteDescription(sdp: SessionDescription) {
        peerConnectionClient?.setRemoteDescription(sdp)
        if (!signalingParameters.initiator!!) {
            // Create answer. Answer SDP will be sent to offering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient?.createAnswer()
        }
    }

    override fun onRemoteIceCandidate(candidate: IceCandidate) {
        peerConnectionClient?.addRemoteIceCandidate(candidate)
    }

    override fun onRemoteIceCandidatesRemoved(candidates: Array<IceCandidate>) {
        peerConnectionClient?.removeRemoteIceCandidates(candidates)
    }

    override fun onChannelClose() {
        disconnectPeerConnection()
    }

    override fun onChannelError(description: String) {

    }

    // Callbacks from PeerConnection internal API to the application
    override fun onPeerConnectionCreated() {
        listener.onPeerConnectionCreated()
    }

    override fun onLocalDescription(sdp: SessionDescription) {
        listener.onLocalDescription(sdp.description, sdp.type.name.toLowerCase())
    }

    override fun onIceCandidate(candidate: IceCandidate) {
        listener.onIceCandidate(gson.toJson(candidate))
    }

    override fun onIceCandidatesRemoved(candidates: Array<IceCandidate>) {
        listener.onIceCandidatesRemoved(gson.toJson(candidates))
    }

    override fun onIceConnected() {
        listener.onIceConnected()
    }

    override fun onIceDisconnected() {
        listener.onIceDisconnected()
    }

    override fun onPeerConnectionClosed() {
        listener.onPeerConnectionClosed()
    }

    override fun onPeerConnectionStatsReady(reports: Array<StatsReport>) {
        listener.onPeerConnectionStatsReady(gson.toJson(reports))
    }

    override fun onPeerConnectionError(description: String) {
        listener.onPeerConnectionError(description)
    }

    private fun createCameraCapturer(context: Context): VideoCapturer? {
        val enumerator: CameraEnumerator = Camera2Enumerator(context)
        val deviceNames = enumerator.deviceNames

        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                // Creating front facing camera capturer
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        // Front facing camera not found, try something else
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                // Creating other camera capturer."
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

}
