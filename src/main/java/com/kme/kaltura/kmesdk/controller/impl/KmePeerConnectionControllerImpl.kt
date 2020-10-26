package com.kme.kaltura.kmesdk.controller.impl

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.kme.kaltura.kmesdk.controller.IKmePeerConnectionController
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionEvents
import com.kme.kaltura.kmesdk.webrtc.peerconnection.KmePeerConnectionClient
import com.kme.kaltura.kmesdk.webrtc.peerconnection.KmePeerConnectionParameters
import com.kme.kaltura.kmesdk.webrtc.signaling.KmeSignalingParameters
import com.kme.kaltura.kmesdk.webrtc.view.KmeLocalVideoSink
import com.kme.kaltura.kmesdk.webrtc.view.KmeRemoteVideoSink
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.message.type.KmeSdpType
import org.webrtc.*
import java.util.*

class KmePeerConnectionControllerImpl(
    private val context: Context,
    private val gson: Gson
) : IKmePeerConnectionController, IKmePeerConnectionEvents {

    private var peerConnectionClient: KmePeerConnectionClient? = null
    private lateinit var peerConnectionParameters: KmePeerConnectionParameters
    private lateinit var signalingParameters: KmeSignalingParameters
    private lateinit var listener: IKmePeerConnectionClientEvents

    private val localVideoSink: KmeLocalVideoSink = KmeLocalVideoSink()
    private var localRendererView: KmeSurfaceRendererView? = null

    private val remoteVideoSink: KmeRemoteVideoSink = KmeRemoteVideoSink()
    private var remoteRendererView: KmeSurfaceRendererView? = null
    private val remoteRendererViews: MutableList<VideoRenderer.Callbacks> = ArrayList()
    private var isPublisher = false
    private var userId = 0L
    private var mediaServerId = 0L

    // Public PeerConnection API
    override fun setTurnServer(turnUrl: String, turnUser: String, turnCred: String) {
        signalingParameters = KmeSignalingParameters()
        buildIceServers(turnUrl, turnUser, turnCred)
    }

    override fun setLocalRenderer(localRenderer: KmeSurfaceRendererView) {
        localRendererView = localRenderer
    }

    override fun setRemoteRenderer(remoteRenderer: KmeSurfaceRendererView) {
        remoteRendererView = remoteRenderer
    }

    override fun createPeerConnection(
        isPublisher: Boolean,
        userId: Long,
        listener: IKmePeerConnectionClientEvents
    ) {
        this.isPublisher = isPublisher
        this.userId = userId
        this.listener = listener

        createConnection()
    }

    override fun createPeerConnection(
        isPublisher: Boolean,
        userId: Long,
        mediaServerId: Long,
        listener: IKmePeerConnectionClientEvents
    ) {
        this.isPublisher = isPublisher
        this.userId = userId
        this.mediaServerId = mediaServerId
        this.listener = listener

        createConnection()
    }

    private fun createConnection() {
        peerConnectionClient = KmePeerConnectionClient()

        localRendererView?.let {
            it.init(peerConnectionClient?.getRenderContext(), null)
            it.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
            it.setEnableHardwareScaler(true)
            it.setMirror(true)
            localVideoSink.setTarget(it)
        }

        remoteRendererView?.let {
            it.init(peerConnectionClient?.getRenderContext(), null)
            it.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
            it.setEnableHardwareScaler(true)
            it.setMirror(true)
            remoteVideoSink.setTarget(it)
            remoteRendererViews.add(remoteVideoSink)
        }

        peerConnectionParameters = KmePeerConnectionParameters()

        peerConnectionClient?.createPeerConnectionFactory(
            context,
            peerConnectionParameters,
            this
        )

        signalingParameters.isPublisher = isPublisher

        var videoCapturer: VideoCapturer? = null
        if (isPublisher && ActivityCompat.checkSelfPermission(
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

    override fun setMediaServerId(mediaServerId: Long) {
        this.mediaServerId = mediaServerId
    }

    override fun createOffer() {
        peerConnectionClient?.createOffer()
    }

    override fun setRemoteSdp(type: KmeSdpType, sdp: String) {
        val sdpType =
            if (type == KmeSdpType.ANSWER) SessionDescription.Type.ANSWER
            else SessionDescription.Type.OFFER
        peerConnectionClient?.setRemoteDescription(SessionDescription(sdpType, sdp))
    }

    override fun enableCamera(isEnable: Boolean) {
        if (isPublisher && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            peerConnectionClient?.setVideoEnabled(isEnable)
        }
    }

    override fun enableAudio(isEnable: Boolean) {
        if (isPublisher && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            peerConnectionClient?.setAudioEnabled(isEnable)
        }
    }

    override fun switchCamera() {
        if (isPublisher && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            peerConnectionClient?.switchCamera()
        }
    }

    override fun disconnectPeerConnection() {
        localVideoSink.setTarget(null)
        remoteVideoSink.setTarget(null)

        if (isPublisher) {
            localRendererView?.release()
            localRendererView = null
        }

        remoteRendererView?.release()
        remoteRendererView = null

        peerConnectionClient?.close()
        peerConnectionClient = null
    }

    // Callbacks from PeerConnection internal API to the application
    override fun onPeerConnectionCreated() {
        listener.onPeerConnectionCreated(userId)
    }

    override fun onLocalDescription(sdp: SessionDescription) {
        listener.onLocalDescription(userId, mediaServerId, sdp.description, sdp.type.name.toLowerCase())
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

    override fun onIceGatheringDone() {
        listener.onIceGatheringDone(userId, mediaServerId)
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
