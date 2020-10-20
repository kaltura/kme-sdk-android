package com.kme.kaltura.kmesdk.controller.impl

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.kme.kaltura.kmesdk.controller.IKmeWebRTCController
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionEvents
import com.kme.kaltura.kmesdk.webrtc.peerconnection.KmePeerConnectionClient
import com.kme.kaltura.kmesdk.webrtc.peerconnection.KmePeerConnectionParameters
import com.kme.kaltura.kmesdk.webrtc.signaling.IKmeSignalingEvents
import com.kme.kaltura.kmesdk.webrtc.signaling.KmeSignalingParameters
import com.kme.kaltura.kmesdk.webrtc.view.KmeLocalVideoSink
import com.kme.kaltura.kmesdk.webrtc.view.KmeRemoteVideoSink
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
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

    private val localVideoSink: KmeLocalVideoSink = KmeLocalVideoSink()
    private var localRendererView: KmeSurfaceRendererView? = null

    private val remoteVideoSink: KmeRemoteVideoSink = KmeRemoteVideoSink()
    private var remoteRendererView: KmeSurfaceRendererView? = null
    private val remoteRendererViews: MutableList<VideoRenderer.Callbacks> = ArrayList()

    // Public PeerConnection API
    override fun createPeerConnection(
        localRenderer: KmeSurfaceRendererView,
        remoteRenderer: KmeSurfaceRendererView,
        turnUrl: String,
        turnUser: String,
        turnCred: String,
        listener: IKmePeerConnectionClientEvents
    ) {
        Log.d(TAG, "createPeerConnection")

        this.listener = listener
        peerConnectionClient = KmePeerConnectionClient()

        localRendererView = localRenderer
        localRendererView?.init(peerConnectionClient?.getRenderContext(), null)
        localRendererView?.setScalingType(ScalingType.SCALE_ASPECT_FILL)
        localRendererView?.setEnableHardwareScaler(true)
        localRendererView?.setMirror(true)
        localVideoSink.setTarget(localRendererView)

        remoteRendererView = remoteRenderer
        remoteRendererView?.init(peerConnectionClient?.getRenderContext(), null)
        remoteRendererView?.setScalingType(ScalingType.SCALE_ASPECT_FILL)
        remoteRendererView?.setEnableHardwareScaler(true)
        remoteRendererView?.setMirror(true)
        remoteVideoSink.setTarget(remoteRendererView)
        remoteRendererViews.add(remoteVideoSink)

        peerConnectionParameters = KmePeerConnectionParameters()

        peerConnectionClient?.createPeerConnectionFactory(
            context,
            peerConnectionParameters,
            this
        )

        signalingParameters = KmeSignalingParameters()

        buildIceServers(turnUrl, turnUser, turnCred)

        var videoCapturer: VideoCapturer? = null
        if (peerConnectionParameters.videoCallEnabled &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            videoCapturer = createCameraCapturer(context)
        }
        peerConnectionClient?.createPeerConnection(
            localVideoSink,
            remoteRendererViews,
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
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "enableCamera $isEnable")
            peerConnectionClient?.setVideoEnabled(isEnable)
            return
        }
        Log.e(TAG, "Camera usage is not allowed")
    }

    override fun enableAudio(isEnable: Boolean) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "enableAudio $isEnable")
            peerConnectionClient?.setAudioEnabled(isEnable)
            return
        }
        Log.e(TAG, "Audio recording usage is not allowed")
    }

    override fun switchCamera() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "switchCamera")
            peerConnectionClient?.switchCamera()
            return
        }
        Log.e(TAG, "Camera usage is not allowed")
    }

    override fun addRenderer() {
        // TODO: not sure need this here. Maybe should be a part of client
    }

    override fun disconnectPeerConnection() {
//        remoteProxyRenderer.setTarget(null)
        localVideoSink.setTarget(null)

        // TODO: not sure need this here. Maybe should be a part of client
        if (localRendererView != null) {
            localRendererView!!.release()
            localRendererView = null
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

    private fun buildIceServers(turnUrl: String, turnUser: String, turnCred: String) {
        val turnsUrl = turnUrl.replace("turn:", "turns:")
        signalingParameters.iceServers.add(
            buildIceServer("$turnUrl:443?transport=udp", turnUser, turnCred)
        )
        signalingParameters.iceServers.add(
            buildIceServer("$turnUrl:443?transport=tcp", turnUser, turnCred)
        )
        signalingParameters.iceServers.add(
            buildIceServer("$turnsUrl:443?transport=tcp", turnUser, turnCred)
        )
        signalingParameters.iceServers.add(
            buildIceServer("$turnUrl:80?transport=udp", turnUser, turnCred)
        )
    }

    private fun buildIceServer(
        serverUrl: String,
        serverUser: String,
        serverCred: String
    ): PeerConnection.IceServer = PeerConnection.IceServer
        .builder(serverUrl)
        .setUsername(serverUser)
        .setPassword(serverCred)
        .createIceServer()

}
