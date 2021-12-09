package com.kme.kaltura.kmesdk.webrtc.peerconnection.impl

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjection
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnection
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.message.type.KmeSdpType
import org.webrtc.*

/**
 * An implementation for p2p connection
 */
internal class KmePeerConnectionImpl(
    private val context: Context,
    private val gson: Gson,
) : IKmePeerConnection, IKmePeerConnectionEvents {

    private var peerConnection: KmeBasePeerConnectionImpl? = null
    private var iceServers: MutableList<PeerConnection.IceServer> = mutableListOf()
    private var listener: IKmePeerConnectionClientEvents? = null

    private var isPublisher: Boolean = false

    private var requestedUserIdStream = ""
    private var mediaServerId = 0L

    private var preferredMicEnabled: Boolean = true
    private var preferredCamEnabled: Boolean = true
    private var preferredFrontCamera: Boolean = true

    // Public PeerConnection API
    /**
     * Setting TURN server for RTC. Build ICE servers collection
     */
    override fun setTurnServer(turnUrl: String, turnUser: String, turnCred: String) {
        iceServers = buildIceServers(turnUrl, turnUser, turnCred)
    }

    /**
     * Set preferred settings for establish p2p connection
     */
    override fun setPreferredSettings(
        micEnabled: Boolean,
        camEnabled: Boolean,
        frontCamEnabled: Boolean,
    ) {
        this.preferredMicEnabled = micEnabled
        this.preferredCamEnabled = camEnabled
        this.preferredFrontCamera = frontCamEnabled
    }

    /**
     * Setting view for stream rendering
     */
    override fun setRenderer(rendererView: KmeSurfaceRendererView) {
        peerConnection?.setRenderer(rendererView)
    }

    override fun removeRenderer() {
        peerConnection?.removeRenderer()
    }

    /**
     * Creates a local video preview
     */
    override fun startPreview(previewRenderer: KmeSurfaceRendererView) {
        peerConnection = KmePreviewPeerConnectionImpl(context, this)

        var videoCapturer: VideoCapturer? = null
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            videoCapturer = createCameraCapturer()
        }

        peerConnection?.startPreview(
            videoCapturer,
            previewRenderer
        )
    }

    /**
     * Creates p2p connection
     */
    override fun createPeerConnection(
        requestedUserIdStream: String,
        isPublisher: Boolean,
        useDataChannel: Boolean,
        listener: IKmePeerConnectionClientEvents,
    ) {
        this.requestedUserIdStream = requestedUserIdStream
        this.listener = listener

        var videoCapturer: VideoCapturer? = null

        if (isPublisher) {
            this.isPublisher = true
            peerConnection = KmePublisherPeerConnectionImpl(context, this)

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                videoCapturer = createCameraCapturer()
            }

            peerConnection?.setPreferredSettings(preferredMicEnabled, preferredCamEnabled)
        } else {
            peerConnection = KmeViewerPeerConnectionImpl(context, this)
        }

        peerConnection?.createPeerConnection(
            videoCapturer,
            useDataChannel,
            iceServers
        )
    }

    override fun startScreenShare(
        requestedUserIdStream: String,
        screenCaptureIntent: Intent,
        listener: IKmePeerConnectionClientEvents,
    ) {
        this.requestedUserIdStream = requestedUserIdStream
        this.listener = listener

        peerConnection = KmeScreenSharePeerConnectionImpl(context, this)
        peerConnection?.createPeerConnection(
            createScreenCapturer(screenCaptureIntent),
            false,
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
        peerConnection?.createOffer()
    }

    /**
     * Setting remote SDP
     */
    override fun setRemoteSdp(type: KmeSdpType, sdp: String) {
        val sdpType =
            if (type == KmeSdpType.ANSWER) SessionDescription.Type.ANSWER
            else SessionDescription.Type.OFFER
        peerConnection?.setRemoteDescription(SessionDescription(sdpType, sdp))
    }

    /**
     * Toggle camera
     */
    override fun enableCamera(isEnable: Boolean) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            peerConnection?.setVideoEnabled(isEnable)
        }
    }

    /**
     * Toggle audio from SDK
     */
    override fun enableAudioInternal(isEnable: Boolean) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            peerConnection?.setAudioEnabledInternal(isEnable)
        }
    }

    /**
     * Toggle audio
     */
    override fun enableAudio(isEnable: Boolean) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            peerConnection?.setAudioEnabled(isEnable)
        }
    }

    /**
     * Switch between existing cameras
     */
    override fun switchCamera() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            preferredFrontCamera = !preferredFrontCamera
            peerConnection?.switchCamera(preferredFrontCamera)
        }
    }

    /**
     * Closes actual p2p connection
     */
    override fun disconnectPeerConnection() {
        peerConnection?.close()
        peerConnection = null
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
    @SuppressLint("DefaultLocale")
    override fun onLocalDescription(sdp: SessionDescription) {
        listener?.onLocalDescription(
            requestedUserIdStream,
            mediaServerId,
            sdp.description,
            sdp.type.name.lowercase()
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
    override fun onUserSpeaking(amplitude: Int) {
        listener?.onUserSpeaking(requestedUserIdStream, amplitude)
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
        listener?.onPeerConnectionClosed(requestedUserIdStream)
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
        listener?.onPeerConnectionError(requestedUserIdStream, description)
    }

    /**
     * Check existed cameras and create video capturer if can
     */
    private fun createCameraCapturer(): VideoCapturer? {
        val enumerator: CameraEnumerator = Camera2Enumerator(context)
        val deviceNames = enumerator.deviceNames

        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName) && preferredFrontCamera) {
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
     * Check existed cameras and create video capturer if can
     */
    private fun createScreenCapturer(screenCaptureIntent: Intent): VideoCapturer {
        return ScreenCapturerAndroid(screenCaptureIntent, object : MediaProjection.Callback() {
            override fun onStop() {
                disconnectPeerConnection()
            }
        })
    }

    /**
     * Build collection of ICE servers to use
     */
    private fun buildIceServers(
        turnUrl: String,
        turnUser: String,
        turnCred: String,
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
        serverCred: String,
    ): PeerConnection.IceServer = PeerConnection.IceServer
        .builder(serverUrl)
        .setUsername(serverUser)
        .setPassword(serverCred)
        .createIceServer()

}
