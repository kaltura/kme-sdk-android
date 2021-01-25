package com.kme.kaltura.kmesdk.controller.impl

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.kme.kaltura.kmesdk.controller.IKmePeerConnectionController
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionEvents
import com.kme.kaltura.kmesdk.webrtc.peerconnection.impl.KmePeerConnectionImpl
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.webrtc.view.KmeVideoSink
import com.kme.kaltura.kmesdk.ws.message.type.KmeSdpType
import org.webrtc.*

/**
 * An implementation for p2p connection
 */
class KmePeerConnectionControllerImpl(
    private val context: Context,
    private val gson: Gson
) : IKmePeerConnectionController, IKmePeerConnectionEvents {

    private var peerConnectionClient: KmePeerConnectionImpl? = null
    private var iceServers: MutableList<PeerConnection.IceServer> = mutableListOf()
    private var listener: IKmePeerConnectionClientEvents? = null

    private val localVideoSink: KmeVideoSink = KmeVideoSink()
    private var localRendererView: KmeSurfaceRendererView? = null

    private val remoteVideoSink: KmeVideoSink = KmeVideoSink()
    private var remoteRendererView: KmeSurfaceRendererView? = null

    private var isPublisher = false
    private var requestedUserIdStream = ""
    private var mediaServerId = 0L

    // Public PeerConnection API
    /**
     * Setting TURN server for RTC. Build ICE servers collection
     */
    override fun setTurnServer(turnUrl: String, turnUser: String, turnCred: String) {
        iceServers = buildIceServers(turnUrl, turnUser, turnCred)
    }

    /**
     * Setting view for local stream rendering
     */
    override fun setLocalRenderer(localRenderer: KmeSurfaceRendererView) {
        localRendererView = localRenderer
    }

    /**
     * Setting view for remote stream rendering
     */
    override fun setRemoteRenderer(remoteRenderer: KmeSurfaceRendererView) {
        remoteRendererView = remoteRenderer
    }

    /**
     * Creates p2p connection
     */
    override fun createPeerConnection(
        isPublisher: Boolean,
        requestedUserIdStream: String,
        listener: IKmePeerConnectionClientEvents
    ) {
        this.isPublisher = isPublisher
        this.requestedUserIdStream = requestedUserIdStream
        this.listener = listener

        peerConnectionClient = KmePeerConnectionImpl()
        var videoCapturer: VideoCapturer? = null

        if (isPublisher) {
            localRendererView?.let {
                it.init(peerConnectionClient?.getRenderContext(), null)
                it.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
                it.setEnableHardwareScaler(true)
                it.setMirror(true)
                localVideoSink.setTarget(it)
            }

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                videoCapturer = createCameraCapturer(context)
            }
        } else {
            remoteRendererView?.let {
                it.init(peerConnectionClient?.getRenderContext(), null)
                it.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
                it.setEnableHardwareScaler(true)
                it.setMirror(false)
                remoteVideoSink.setTarget(it)
            }
        }

        peerConnectionClient?.createPeerConnectionFactory(context, this)
        peerConnectionClient?.createPeerConnection(
            context,
            localVideoSink,
            remoteVideoSink,
            videoCapturer,
            isPublisher,
            iceServers
        )
    }

    /**
     * Setting media server id for data relay
     */
    override fun setMediaServerId(mediaServerId: Long) {
        this.mediaServerId = mediaServerId
    }

    /**
     * Creates an offers
     */
    override fun createOffer() {
        peerConnectionClient?.createOffer()
    }

    /**
     * Setting remote SDP
     */
    override fun setRemoteSdp(type: KmeSdpType, sdp: String) {
        val sdpType =
            if (type == KmeSdpType.ANSWER) SessionDescription.Type.ANSWER
            else SessionDescription.Type.OFFER
        peerConnectionClient?.setRemoteDescription(SessionDescription(sdpType, sdp))
    }

    /**
     * Toggle camera
     */
    override fun enableCamera(isEnable: Boolean) {
        if (isPublisher && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            peerConnectionClient?.setVideoEnabled(isEnable)
        }
    }

    /**
     * Toggle audio
     */
    override fun enableAudio(isEnable: Boolean) {
        if (isPublisher && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            peerConnectionClient?.setAudioEnabled(isEnable)
        }
    }

    /**
     * Switch between existing cameras
     */
    override fun switchCamera() {
        if (isPublisher && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            peerConnectionClient?.switchCamera()
        }
    }

    /**
     * Closes actual p2p connection
     */
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
    /**
     * Callback fired once peerConnection instance created
     */
    override fun onPeerConnectionCreated() {
        listener?.onPeerConnectionCreated(requestedUserIdStream)
    }

    /**
     * Callback fired once local SDP is created and set
     */
    override fun onLocalDescription(sdp: SessionDescription) {
        listener?.onLocalDescription(
            requestedUserIdStream,
            mediaServerId,
            sdp.description,
            sdp.type.name.toLowerCase()
        )
    }

    /**
     * Callback fired once local Ice candidate is generated
     */
    override fun onIceCandidate(candidate: IceCandidate) {
        listener?.onIceCandidate(gson.toJson(candidate))
    }

    /**
     * Callback fired once local ICE candidates are removed
     */
    override fun onIceCandidatesRemoved(candidates: Array<IceCandidate>) {
        listener?.onIceCandidatesRemoved(gson.toJson(candidates))
    }

    /**
     * Callback fired once connection is established (IceConnectionState is
     * CONNECTED)
     */
    override fun onIceConnected() {
        listener?.onIceConnected()
    }

    /**
     * Callback fired once ice gathering is complete (IceGatheringDone is COMPLETE)
     */
    override fun onIceGatheringDone() {
        listener?.onIceGatheringDone(requestedUserIdStream, mediaServerId)
    }

    /**
     * Callback fired to indicate current talking user
     */
    override fun onUserSpeaking(isSpeaking: Boolean) {
        listener?.onUserSpeaking(requestedUserIdStream, isSpeaking)
    }

    /**
     * Callback fired once connection is closed (IceConnectionState is
     * DISCONNECTED)
     */
    override fun onIceDisconnected() {
        listener?.onIceDisconnected()
    }

    /**
     * Callback fired once peer connection is closed
     */
    override fun onPeerConnectionClosed() {
        listener?.onPeerConnectionClosed()
    }

    /**
     * Callback fired once peer connection statistics is ready
     */
    override fun onPeerConnectionStatsReady(reports: Array<StatsReport>) {
        listener?.onPeerConnectionStatsReady(gson.toJson(reports))
    }

    /**
     * Callback fired once peer connection error happened
     */
    override fun onPeerConnectionError(description: String) {
        listener?.onPeerConnectionError(description)
    }

    /**
     * Check existed cameras and create video capturer if can
     */
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

    /**
     * Build collection of ICE servers to use
     */
    private fun buildIceServers(
        turnUrl: String,
        turnUser: String,
        turnCred: String
    ): MutableList<PeerConnection.IceServer> {
        val iceServers: MutableList<PeerConnection.IceServer> = mutableListOf()
        val turnsUrl = turnUrl.replace("turn:", "turns:")

        iceServers.add(
            buildIceServer("$turnUrl:443?transport=udp", turnUser, turnCred)
        )
        iceServers.add(
            buildIceServer("$turnUrl:443?transport=tcp", turnUser, turnCred)
        )
        iceServers.add(
            buildIceServer("$turnsUrl:443?transport=tcp", turnUser, turnCred)
        )
        iceServers.add(
            buildIceServer("$turnUrl:80?transport=udp", turnUser, turnCred)
        )
        return iceServers
    }

    /**
     * Build ICE server based on input data
     */
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
