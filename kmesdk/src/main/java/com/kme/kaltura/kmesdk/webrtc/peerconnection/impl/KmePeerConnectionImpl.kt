package com.kme.kaltura.kmesdk.webrtc.peerconnection.impl

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnection
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionEvents
import com.kme.kaltura.kmesdk.webrtc.stats.KmeSoundAmplitudeListener
import com.kme.kaltura.kmesdk.webrtc.stats.KmeSoundAmplitudeMeter
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import org.webrtc.*
import org.webrtc.PeerConnection.*
import org.webrtc.audio.JavaAudioDeviceModule.builder
import org.webrtc.voiceengine.WebRtcAudioManager
import org.webrtc.voiceengine.WebRtcAudioUtils
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*

/**
 * An implementation actions under WebRTC peer connection object
 */
class KmePeerConnectionImpl : IKmePeerConnection, KmeSoundAmplitudeListener {

    private val localSdpObserver: LocalSdpObserver = LocalSdpObserver()
    private val remoteSdpObserver: RemoteSdpObserver = RemoteSdpObserver()
    private val pcObserver: PeerConnectionObserver = PeerConnectionObserver()

    private val rootEglBase: EglBase = EglBase.create()

    private var factory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var soundAmplitudeMeter: KmeSoundAmplitudeMeter? = null
    private var iceServers: MutableList<IceServer> = mutableListOf()
    private var isPublisher = false
    private var useDataChannel = false
    private var events: IKmePeerConnectionEvents? = null

    private var videoCapturerStopped = false
    private var preferredMicEnabled: Boolean = true
    private var preferredCamEnabled: Boolean = true

    private var queuedRemoteCandidates: MutableList<IceCandidate>? = null
    private lateinit var localSdp: SessionDescription

    private var videoCapturer: VideoCapturer? = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null

    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null
    private var localVideoSource: VideoSource? = null
    private var localAudioSource: AudioSource? = null
    private var localVideoSender: RtpSender? = null
    private lateinit var localVideoSink: VideoSink

    private var remoteVideoTrack: VideoTrack? = null
    private lateinit var remoteVideoSink: VideoSink

    private var audioConstraints: MediaConstraints? = null
    private var sdpMediaConstraints: MediaConstraints? = null
    private var volumeDataChannel: DataChannel? = null

    /**
     * Creates a local video preview
     */
    override fun startPreview(
        context: Context,
        videoCapturer: VideoCapturer?,
        previewRenderer: KmeSurfaceRendererView
    ) {
        this.videoCapturer = videoCapturer
        this.localVideoSink = previewRenderer

        peerConnection = factory?.createPeerConnection(RTCConfiguration(listOf()), pcObserver)
        peerConnection?.let {
            if (videoCapturer != null) {
                it.addTrack(createLocalVideoTrack(context, videoCapturer), listOf("ARDAMS"))
            }
        }
    }

    /**
     * Set preferred settings for establish p2p connection
     */
    override fun setPreferredSettings(
        preferredMicEnabled: Boolean,
        preferredCamEnabled: Boolean
    ) {
        this.preferredMicEnabled = preferredMicEnabled
        this.preferredCamEnabled = preferredCamEnabled
    }

    /**
     * Creates peer connection factory
     */
    override fun createPeerConnectionFactory(
        context: Context,
        events: IKmePeerConnectionEvents
    ) {
        this.events = events

        var fieldTrials = VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL
        fieldTrials = "$fieldTrials $VIDEO_FRAME_EMIT_FIELDTRIAL"

        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setFieldTrials(fieldTrials)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )

        WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(true)
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(false)
        WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(false)
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(false)

        val encoderFactory = DefaultVideoEncoderFactory(
            getRenderContext(),
            true,
            false
        )
        val decoderFactory = DefaultVideoDecoderFactory(getRenderContext())

        factory = PeerConnectionFactory.builder()
            .setAudioDeviceModule(builder(context).createAudioDeviceModule())
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()
    }

    /**
     * Creates peer connection
     */
    override fun createPeerConnection(
        context: Context,
        localVideoSink: VideoSink,
        remoteVideoSink: VideoSink,
        videoCapturer: VideoCapturer?,
        isPublisher: Boolean,
        useDataChannel: Boolean,
        iceServers: MutableList<IceServer>
    ) {
        this.localVideoSink = localVideoSink
        this.remoteVideoSink = remoteVideoSink
        this.videoCapturer = videoCapturer
        this.isPublisher = isPublisher
        this.useDataChannel = useDataChannel
        this.iceServers = iceServers

        createMediaConstraints()
        createPeerConnection(context)
    }

    /**
     * Audio and SDP constraints
     */
    private fun createMediaConstraints() {
        audioConstraints = MediaConstraints()
        audioConstraints?.mandatory?.add(
            MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "true")
        )
        audioConstraints?.mandatory?.add(
            MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "true")
        )
        audioConstraints?.mandatory?.add(
            MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "true")
        )
        audioConstraints?.mandatory?.add(
            MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "true")
        )
        audioConstraints?.mandatory?.add(
            MediaConstraints.KeyValuePair(AUDIO_LEVEL_CONTROL_CONSTRAINT, "true")
        )

        sdpMediaConstraints = MediaConstraints()
        sdpMediaConstraints?.mandatory?.add(
            MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
        )
        sdpMediaConstraints?.mandatory?.add(
            MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")
        )
    }

    /**
     * Creates peer connection
     */
    private fun createPeerConnection(context: Context) {
        if (factory == null) {
            return
        }

        queuedRemoteCandidates = ArrayList()

        val rtcConfig = RTCConfiguration(iceServers)
        peerConnection = factory?.createPeerConnection(rtcConfig, pcObserver)

        peerConnection?.let {

            // Set INFO libjingle logging. NOTE: this _must_ happen while |factory| is alive!
            Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO)

            val mediaStreamLabels = listOf("ARDAMS")
            if (videoCapturer != null) {
                it.addTrack(
                    createLocalVideoTrack(context, videoCapturer),
                    mediaStreamLabels
                )
            }

            it.addTrack(createLocalAudioTrack(), mediaStreamLabels)

            if (videoCapturer != null) findVideoSender()

            if (isPublisher) {
                val volumeInit = DataChannel.Init()
                volumeInit.ordered = false
                volumeInit.maxRetransmits = 0
                if (useDataChannel) {
                    volumeDataChannel = it.createDataChannel("volumeDataChannel", volumeInit)
                }

                soundAmplitudeMeter = KmeSoundAmplitudeMeter(it, this)
            }

            events?.onPeerConnectionCreated()
        }
    }

    /**
     * Creates local audio track
     */
    private fun createLocalAudioTrack(): AudioTrack? {
        localAudioSource = factory?.createAudioSource(audioConstraints)
        localAudioTrack = factory?.createAudioTrack(AUDIO_TRACK_ID, localAudioSource)
        localAudioTrack?.setEnabled(preferredMicEnabled)
        return localAudioTrack
    }

    /**
     * Creates local video track
     */
    private fun createLocalVideoTrack(context: Context, capturer: VideoCapturer?): VideoTrack? {
        capturer?.let {
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", getRenderContext())
            localVideoSource = factory?.createVideoSource(it.isScreencast)
            it.initialize(surfaceTextureHelper, context, localVideoSource?.capturerObserver)
            it.startCapture(VIDEO_WIDTH, VIDEO_HEIGHT, VIDEO_FPS)
        }

        localVideoTrack = factory?.createVideoTrack(VIDEO_TRACK_ID, localVideoSource)
        localVideoTrack?.setEnabled(preferredCamEnabled)
        localVideoTrack?.addSink(localVideoSink)
        return localVideoTrack
    }

    /**
     * Find local video sender
     */
    private fun findVideoSender() {
        for (sender in peerConnection?.senders!!) {
            if (sender.track() != null) {
                val trackType = sender.track()?.kind()
                if (trackType == VIDEO_TRACK_TYPE) {
                    localVideoSender = sender
                }
            }
        }
    }

    /**
     * Toggle audio
     */
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun setAudioEnabled(enable: Boolean) {
        preferredMicEnabled = enable
        if (enable) soundAmplitudeMeter?.startMeasure() else soundAmplitudeMeter?.stopMeasure()
        localAudioTrack?.setEnabled(enable)
    }

    /**
     * Toggle video
     */
    @RequiresPermission(Manifest.permission.CAMERA)
    override fun setVideoEnabled(enable: Boolean) {
        localVideoTrack?.setEnabled(enable)
    }

    /**
     * Creates an offers
     */
    override fun createOffer() {
        peerConnection?.createOffer(localSdpObserver, sdpMediaConstraints)
    }

    /**
     * Creates an answer
     */
    override fun createAnswer() {
        peerConnection?.createAnswer(localSdpObserver, sdpMediaConstraints)
    }

    /**
     * Handle adding ICE candidate
     */
    override fun addRemoteIceCandidate(candidate: IceCandidate?) {
        if (queuedRemoteCandidates != null) {
            queuedRemoteCandidates?.add(candidate!!)
        } else {
            peerConnection?.addIceCandidate(candidate)
        }
    }

    /**
     * Handle remove remote ICE candidates
     */
    override fun removeRemoteIceCandidates(candidates: Array<IceCandidate>) {
        if (peerConnection == null) {
            return
        }
        drainCandidates()
        peerConnection?.removeIceCandidates(candidates)
    }

    /**
     * Setting remote SDP
     */
    override fun setRemoteDescription(sdp: SessionDescription) {
        peerConnection?.setRemoteDescription(remoteSdpObserver, sdp)
    }

    /**
     * Disable outgoing video stream
     */
    override fun stopVideoSource() {
        if (!videoCapturerStopped) {
            try {
                videoCapturer?.stopCapture()
            } catch (e: InterruptedException) {

            }
            videoCapturerStopped = true
        }
    }

    /**
     * Enable outgoing video stream
     */
    override fun startVideoSource() {
        if (videoCapturerStopped) {
            videoCapturer?.startCapture(VIDEO_WIDTH, VIDEO_HEIGHT, VIDEO_FPS)
            videoCapturerStopped = false
        }
    }

    /**
     * Change bitrate parameters of local video
     */
    fun setVideoMaxBitrate(maxBitrateKbps: Int?) {
        if (peerConnection == null || localVideoSender == null) {
            return
        }
        if (localVideoSender == null) {
            return
        }
        val parameters = localVideoSender!!.parameters
        if (parameters.encodings.size == 0) {
            return
        }
        for (encoding in parameters.encodings) {
            encoding.maxBitrateBps =
                if (maxBitrateKbps == null) null else maxBitrateKbps * BPS_IN_KBPS
        }

        if (!localVideoSender!!.setParameters(parameters)) {
            //TODO handle parameters are not set
        }
    }

    /**
     * Clear list of ICE candidates
     */
    private fun drainCandidates() {
        if (queuedRemoteCandidates != null) {
            for (candidate in queuedRemoteCandidates!!) {
                peerConnection?.addIceCandidate(candidate)
            }
            queuedRemoteCandidates = null
        }
    }

    /**
     * Switch between existing cameras
     */
    @RequiresPermission(Manifest.permission.CAMERA)
    override fun switchCamera() {
        videoCapturer?.let {
            if (it is CameraVideoCapturer) {
                it.switchCamera(null)
            }
        }
    }

    /**
     * Change capture parameters of local video
     */
    private fun changeCaptureFormat(
        width: Int,
        height: Int,
        frameRate: Int
    ) {
        if (videoCapturer == null) return

        localVideoSource?.adaptOutputFormat(width, height, frameRate)
    }

    /**
     * Closes actual connection
     */
    override fun close() {
        if (isPublisher) {
            soundAmplitudeMeter?.stopMeasure()
            soundAmplitudeMeter = null
        }

        volumeDataChannel?.unregisterObserver()
        volumeDataChannel?.close()
        volumeDataChannel?.dispose()
        volumeDataChannel = null

        localAudioSource?.dispose()
        localAudioSource = null

        try {
            videoCapturer?.stopCapture()
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
        videoCapturerStopped = true
        videoCapturer?.dispose()
        videoCapturer = null

        localVideoSource?.dispose()
        localVideoSource = null

        surfaceTextureHelper?.dispose()
        surfaceTextureHelper = null

        peerConnection?.close()
        peerConnection?.dispose()
        peerConnection = null

        factory?.dispose()
        factory = null

        events?.onPeerConnectionClosed()
        PeerConnectionFactory.stopInternalTracingCapture()
        PeerConnectionFactory.shutdownInternalTracer()
        events = null
    }

    /**
     * Getting rendering context for WebRTC
     */
    override fun getRenderContext(): EglBase.Context? {
        return rootEglBase.eglBaseContext
    }

    /**
     * Fired once sound amplitude measured
     */
    override fun onAmplitudeMeasured(amplitude: Int) {
        events?.onUserSpeaking(amplitude)

        volumeDataChannel?.let {
            val data = (if (amplitude > 150) "1" else "0") + ",$amplitude"
            val buffer: ByteBuffer = ByteBuffer.wrap(data.toByteArray())
            it.send(DataChannel.Buffer(buffer, false))
        }
    }

    /**
     * Peer connection callbacks
     */
    private inner class PeerConnectionObserver : PeerConnection.Observer {

        override fun onIceCandidate(candidate: IceCandidate) {
            events?.onIceCandidate(candidate)
        }

        override fun onIceCandidatesRemoved(candidates: Array<IceCandidate>) {
            events?.onIceCandidatesRemoved(candidates)
        }

        override fun onSignalingChange(newState: SignalingState) {
            val test = ""
        }

        override fun onIceConnectionChange(newState: IceConnectionState) {
            when (newState) {
                IceConnectionState.CONNECTED -> {
                    if (isPublisher && preferredMicEnabled) soundAmplitudeMeter?.startMeasure()
                    events?.onIceConnected()
                }
                IceConnectionState.COMPLETED -> {
                    events?.onIceGatheringDone()
                }
                IceConnectionState.DISCONNECTED -> {
                    events?.onIceDisconnected()
                }
                IceConnectionState.FAILED -> {
                    events?.onPeerConnectionError("ICE connection failed.")
                }
                else -> {
                }
            }
        }

        override fun onIceGatheringChange(newState: IceGatheringState) {
            val test = ""
        }

        override fun onIceConnectionReceivingChange(receiving: Boolean) {
            val test = ""
        }

        override fun onAddStream(stream: MediaStream) {
            if (stream.audioTracks.size > 1 || stream.videoTracks.size > 1) {
                return
            }

            if (stream.videoTracks.size == 1) {
                remoteVideoTrack = stream.videoTracks[0]
                remoteVideoTrack?.setEnabled(true)
                remoteVideoTrack?.addSink(remoteVideoSink)
            }
        }

        override fun onRemoveStream(stream: MediaStream) {
            remoteVideoTrack = null
        }

        /**
         * Fired once data channel created
         */
        override fun onDataChannel(dataChannel: DataChannel) {
            if (useDataChannel) {
                volumeDataChannel = dataChannel
                volumeDataChannel?.registerObserver(object : DataChannel.Observer {

                    override fun onBufferedAmountChange(previousAmount: Long) {}

                    override fun onStateChange() {}

                    override fun onMessage(buffer: DataChannel.Buffer) {
                        if (buffer.binary) {
                            return
                        }
                        val byteBuffer = buffer.data
                        val bytes = ByteArray(byteBuffer.capacity())
                        byteBuffer[bytes]

                        val volumeData = String(bytes, Charset.forName("UTF-8"))
                            .split(",")
                        events?.onUserSpeaking(volumeData[1].toInt())
                    }
                })
            }
        }

        override fun onRenegotiationNeeded() {
            val test = ""
        }

        override fun onAddTrack(receiver: RtpReceiver, mediaStreams: Array<MediaStream>) {
            val test = ""
        }
    }

    /**
     * Local SDP callbacks
     */
    private inner class LocalSdpObserver : SdpObserver {

        override fun onCreateSuccess(sdp: SessionDescription) {
            localSdp = sdp
            peerConnection?.setLocalDescription(this, sdp)
        }

        override fun onSetSuccess() {
            events?.onLocalDescription(localSdp)
        }

        override fun onCreateFailure(error: String) {}

        override fun onSetFailure(error: String) {}
    }

    /**
     * Remote SDP callbacks
     */
    private inner class RemoteSdpObserver : SdpObserver {

        override fun onCreateSuccess(origSdp: SessionDescription) {}

        override fun onSetSuccess() {
            if (!isPublisher) {
                peerConnection?.createAnswer(localSdpObserver, sdpMediaConstraints)
            }
        }

        override fun onCreateFailure(error: String) {}

        override fun onSetFailure(error: String) {}
    }

    companion object {
        const val AUDIO_TRACK_ID = "ARDAMSa0"

        private const val VIDEO_TRACK_ID = "ARDAMSv0"
        private const val VIDEO_TRACK_TYPE = "video"
        private const val VIDEO_CODEC_VP8 = "VP8"
        private const val VIDEO_CODEC_VP9 = "VP9"
        private const val VIDEO_CODEC_H264 = "H264"
        private const val VIDEO_CODEC_H264_BASELINE = "H264 Baseline"
        private const val VIDEO_CODEC_H264_HIGH = "H264 High"
        private const val AUDIO_CODEC_OPUS = "opus"
        private const val AUDIO_CODEC_ISAC = "ISAC"
        private const val VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate"
        private const val VIDEO_FLEXFEC_FIELDTRIAL =
            "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/"
        private const val VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/"
        private const val VIDEO_H264_HIGH_PROFILE_FIELDTRIAL = "WebRTC-H264HighProfile/Enabled/"
        private const val DISABLE_WEBRTC_AGC_FIELDTRIAL =
            "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/"
        private const val VIDEO_FRAME_EMIT_FIELDTRIAL: String = "VideoFrameEmit/Enabled/"
        private const val AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate"
        private const val AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation"
        private const val AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl"
        private const val AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter"
        private const val AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression"
        private const val AUDIO_LEVEL_CONTROL_CONSTRAINT = "levelControl"
        private const val DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement"
        private const val VIDEO_WIDTH = 720 /*1280*/
        private const val VIDEO_HEIGHT = 480 /*720*/
        private const val VIDEO_FPS = 30
        private const val BPS_IN_KBPS = 1000
    }

}
