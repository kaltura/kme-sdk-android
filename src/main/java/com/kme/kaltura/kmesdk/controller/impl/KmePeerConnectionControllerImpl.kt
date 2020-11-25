package com.kme.kaltura.kmesdk.controller.impl

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.kme.kaltura.kmesdk.controller.IKmePeerConnectionController
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionEvents
import com.kme.kaltura.kmesdk.webrtc.peerconnection.impl.KmePeerConnectionImpl
import com.kme.kaltura.kmesdk.webrtc.stats.KmeSoundAmplitudeMeter
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.webrtc.view.KmeVideoSink
import com.kme.kaltura.kmesdk.ws.message.type.KmeSdpType
import org.webrtc.*

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

    private var soundMeter: KmeSoundAmplitudeMeter? = null
    private var meterHandler = Handler()
    private var meterReporter = Handler(Looper.getMainLooper())

    private var isPublisher = false
    private var userId = 0L
    private var mediaServerId = 0L

    // Public PeerConnection API
    override fun setTurnServer(turnUrl: String, turnUser: String, turnCred: String) {
        iceServers = buildIceServers(turnUrl, turnUser, turnCred)
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

            soundMeter = KmeSoundAmplitudeMeter(context)
        } else {
            remoteRendererView?.let {
                it.init(peerConnectionClient?.getRenderContext(), null)
                it.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
                it.setEnableHardwareScaler(true)
                it.setMirror(true)
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

    private val soundMeasureRunnable = Runnable { measureSound() }

    private fun reportUserSpeakingRunnable(isSpeaking: Boolean) = Runnable {
        listener?.onUserSpeaking(userId, isSpeaking)
    }

    private fun measureSound() {
        soundMeter?.getAmplitude()?.let {
            val bringToFront = it > SOUND_METER_VALUE_TO_DETECT
            meterReporter.post(reportUserSpeakingRunnable(bringToFront))
            peerConnectionClient?.setAudioAmplitude(bringToFront, it)
        }
        meterHandler.postDelayed(soundMeasureRunnable, SOUND_METER_DELAY)
    }

    private fun startMeasure() {
        soundMeter?.start()
        meterHandler.post(soundMeasureRunnable)
    }

    private fun stopMeasure() {
        meterHandler.removeCallbacks(soundMeasureRunnable)
        soundMeter?.stop()
        meterReporter.post(reportUserSpeakingRunnable(false))
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
            if (isEnable) startMeasure() else stopMeasure()
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
        stopMeasure()

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
        listener?.onPeerConnectionCreated(userId)
    }

    override fun onLocalDescription(sdp: SessionDescription) {
        listener?.onLocalDescription(
            userId,
            mediaServerId,
            sdp.description,
            sdp.type.name.toLowerCase()
        )
    }

    override fun onIceCandidate(candidate: IceCandidate) {
        listener?.onIceCandidate(gson.toJson(candidate))
    }

    override fun onIceCandidatesRemoved(candidates: Array<IceCandidate>) {
        listener?.onIceCandidatesRemoved(gson.toJson(candidates))
    }

    override fun onIceConnected() {
        listener?.onIceConnected()
    }

    override fun onIceGatheringDone() {
        if (isPublisher) startMeasure()
        listener?.onIceGatheringDone(userId, mediaServerId)
    }

    override fun onUserSpeaking(isSpeaking: Boolean) {
        meterReporter.post(reportUserSpeakingRunnable(isSpeaking))
    }

    override fun onIceDisconnected() {
        listener?.onIceDisconnected()
    }

    override fun onPeerConnectionClosed() {
        listener?.onPeerConnectionClosed()
    }

    override fun onPeerConnectionStatsReady(reports: Array<StatsReport>) {
        listener?.onPeerConnectionStatsReady(gson.toJson(reports))
    }

    override fun onPeerConnectionError(description: String) {
        listener?.onPeerConnectionError(description)
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

    private fun buildIceServer(
        serverUrl: String,
        serverUser: String,
        serverCred: String
    ): PeerConnection.IceServer = PeerConnection.IceServer
        .builder(serverUrl)
        .setUsername(serverUser)
        .setPassword(serverCred)
        .createIceServer()

    companion object {

        private const val SOUND_METER_DELAY: Long = 500
        private const val SOUND_METER_VALUE_TO_DETECT = 1200

    }

}
