package com.kme.kaltura.kmesdk.webrtc.peerconnection.impl

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmeBasePeerConnection
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionEvents
import com.kme.kaltura.kmesdk.webrtc.stats.KmeSoundAmplitudeMeter
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import org.webrtc.*
import org.webrtc.PeerConnection.*
import org.webrtc.audio.JavaAudioDeviceModule.builder
import org.webrtc.voiceengine.WebRtcAudioManager
import org.webrtc.voiceengine.WebRtcAudioUtils
import java.nio.charset.Charset

/**
 * An implementation actions under WebRTC peer connection object
 */
open class KmeBasePeerConnectionImpl(
    internal val context: Context,
    internal var events: IKmePeerConnectionEvents? = null
) : IKmeBasePeerConnection {

    internal val localSdpObserver: LocalSdpObserver = LocalSdpObserver()
    internal val remoteSdpObserver: RemoteSdpObserver = RemoteSdpObserver()
    internal val pcObserver: PeerConnectionObserver = PeerConnectionObserver()
    internal lateinit var localSdp: SessionDescription

    internal var factory: PeerConnectionFactory? = null
    internal var peerConnection: PeerConnection? = null
    internal var soundAmplitudeMeter: KmeSoundAmplitudeMeter? = null
    internal var iceServers: MutableList<IceServer> = mutableListOf()
    private var remoteCandidates: MutableList<IceCandidate> = arrayListOf()

    internal var isPublisher = false
    internal var isScreenShare = false
    internal var useDataChannel = false

    internal var videoCapturerEnabled = false
    internal var preferredMicEnabled = true
    internal var preferredCamEnabled = true

    internal var rootEglBase: EglBase? = EglBase.create()
    internal var videoCapturer: VideoCapturer? = null
    internal var surfaceTextureHelper: SurfaceTextureHelper? = null

    internal var localVideoTrack: VideoTrack? = null
    internal var localAudioTrack: AudioTrack? = null
    private var localVideoSource: VideoSource? = null
    private var localAudioSource: AudioSource? = null
    private var localVideoSender: RtpSender? = null

    internal var remoteVideoTrack: VideoTrack? = null
    internal var remoteAudioTrack: AudioTrack? = null
    internal var rendererView: KmeSurfaceRendererView? = null

    internal var audioConstraints = MediaConstraints()
    internal var sdpMediaConstraints = MediaConstraints()
    internal var volumeDataChannel: DataChannel? = null

    init {
        val fieldTrials = "$VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL $VIDEO_FRAME_EMIT_FIELDTRIAL"
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
            .setAudioDeviceModule(
                builder(context).createAudioDeviceModule()
            )
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()
    }

    /**
     * Creates a local video preview
     */
    override fun startPreview(
        videoCapturer: VideoCapturer?,
        rendererView: KmeSurfaceRendererView
    ) {
        throw Exception("Wrong state.")
    }

    /**
     * Set preferred settings for establish p2p connection
     */
    override fun setPreferredSettings(
        preferredMicEnabled: Boolean,
        preferredCamEnabled: Boolean
    ) {
        throw Exception("Wrong state.")
    }

    /**
     * Creates peer connection
     */
    override fun createPeerConnection(
        videoCapturer: VideoCapturer?,
        useDataChannel: Boolean,
        iceServers: MutableList<IceServer>
    ) {
        this.videoCapturer = videoCapturer
        this.useDataChannel = useDataChannel
        this.iceServers = iceServers

        peerConnection = factory?.createPeerConnection(RTCConfiguration(iceServers), pcObserver)
        peerConnection?.let {
            // Set INFO libjingle logging. NOTE: this _must_ happen while |factory| is alive!
            Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO)

            if (!isScreenShare) {
                addMediaConstraints()
                it.addTrack(createLocalAudioTrack(), videoStreamId)
            }
        }
    }

    override fun setRenderer(rendererView: KmeSurfaceRendererView) {
        throw Exception("Wrong state.")
    }

    override fun removeRenderer() {
        throw Exception("Wrong state.")
    }

    /**
     * Audio and SDP constraints
     */
    private fun addMediaConstraints() {
        audioConstraints.mandatory?.add(
            MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "true")
        )
        audioConstraints.mandatory?.add(
            MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "true")
        )
        audioConstraints.mandatory?.add(
            MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "true")
        )
        audioConstraints.mandatory?.add(
            MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "true")
        )
        audioConstraints.mandatory?.add(
            MediaConstraints.KeyValuePair(AUDIO_LEVEL_CONTROL_CONSTRAINT, "true")
        )

        sdpMediaConstraints.mandatory?.add(
            MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
        )
        sdpMediaConstraints.mandatory?.add(
            MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")
        )
    }

    /**
     * Creates local audio track
     */
    private fun createLocalAudioTrack(): AudioTrack? {
        localAudioSource = factory?.createAudioSource(audioConstraints)
        localAudioTrack = factory?.createAudioTrack(AUDIO_TRACK_ID, localAudioSource)?.also {
            it.setEnabled(preferredMicEnabled)
        }
        return localAudioTrack
    }

    /**
     * Creates local video track
     */
    fun createLocalVideoTrack(videoCapturer: VideoCapturer?): VideoTrack? {
        videoCapturer?.let { capturer ->
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", getRenderContext())
            localVideoSource = factory?.createVideoSource(capturer.isScreencast)
//            isScreenShare = capturer.isScreencast
            capturer.initialize(surfaceTextureHelper, context, localVideoSource?.capturerObserver)

            if (isScreenShare) {
                capturer.startCapture(
                    SCREEN_SHARE_VIDEO_WIDTH,
                    SCREEN_SHARE_VIDEO_HEIGHT,
                    SCREEN_SHARE_VIDEO_FPS
                )
            } else {
                capturer.startCapture(
                    VIDEO_WIDTH,
                    VIDEO_HEIGHT,
                    VIDEO_FPS
                )
            }
        }

        localVideoTrack = factory?.createVideoTrack(VIDEO_TRACK_ID, localVideoSource)?.apply {
            setEnabled(preferredCamEnabled)
            rendererView?.let {
                addSink(it)
            }
        }
        return localVideoTrack
    }

    /**
     * Find local video sender
     */
    fun findVideoSender() {
        peerConnection?.senders?.let { senders ->
            senders.forEach { sender ->
                sender?.track()?.let {
                    if (sender.track()?.kind() == VIDEO_TRACK_TYPE) {
                        localVideoSender = sender
                    }
                }
            }
        }
    }

    val videoStreamId = listOf("ARDAMS")

    /**
     * Toggle audio from SDK
     */
    override fun setAudioEnabledInternal(enable: Boolean) {
        throw Exception("Wrong state.")
    }

    /**
     * Toggle audio
     */
    override fun setAudioEnabled(enable: Boolean) {
        throw Exception("Wrong state.")
    }

    /**
     * Toggle video
     */
    override fun setVideoEnabled(enable: Boolean) {
        throw Exception("Wrong state.")
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
        candidate?.let {
            remoteCandidates.add(it)
        }
//        peerConnection?.addIceCandidate(candidate)
    }

    /**
     * Handle remove remote ICE candidates
     */
    override fun removeRemoteIceCandidates(candidates: Array<IceCandidate>) {
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
     * Enable/Disable outgoing video stream
     */
    override fun enableVideoSource(enable: Boolean) {
        throw Exception("Wrong state.")
    }

    /**
     * Change bitrate parameters of local video
     */
    fun setVideoMaxBitrate(maxBitrateKbps: Int?) {
        if (peerConnection == null || localVideoSender == null || localVideoSender == null) {
            return
        }

        localVideoSender?.let {
            val parameters = it.parameters
            if (parameters.encodings.size == 0) {
                return
            }

            for (encoding in parameters.encodings) {
                encoding.maxBitrateBps =
                    if (maxBitrateKbps == null) null else maxBitrateKbps * BPS_IN_KBPS
            }

            if (!it.setParameters(parameters)) {
                //TODO handle parameters are not set
            }
        }
    }

    /**
     * Clear list of ICE candidates
     */
    private fun drainCandidates() {
        remoteCandidates.forEach {
            peerConnection?.addIceCandidate(it)
        }
        remoteCandidates.clear()
    }

    /**
     * Switch between existing cameras
     */
    @RequiresPermission(Manifest.permission.CAMERA)
    override fun switchCamera(frontCamera: Boolean) {
        throw Exception("Wrong state.")
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

        removeRenderer()

        localAudioSource?.dispose()
        localAudioSource = null

        try {
            videoCapturer?.stopCapture()
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
        videoCapturerEnabled = false
        videoCapturer?.dispose()
        videoCapturer = null

        localVideoSource?.dispose()
        localVideoSource = null

        surfaceTextureHelper?.dispose()
        surfaceTextureHelper = null

        peerConnection?.close()
        peerConnection?.dispose()
        peerConnection = null

        rootEglBase?.apply {
            releaseSurface()
            detachCurrent()
            release()
        }
        rootEglBase = null

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
    final override fun getRenderContext(): EglBase.Context? {
        return rootEglBase?.eglBaseContext
    }

    /**
     * Peer connection callbacks
     */
    internal inner class PeerConnectionObserver : Observer {

        override fun onIceCandidate(candidate: IceCandidate) {
            events?.onIceCandidate(candidate)
        }

        override fun onIceCandidatesRemoved(candidates: Array<IceCandidate>) {
            events?.onIceCandidatesRemoved(candidates)
        }

        override fun onSignalingChange(newState: SignalingState) {

        }

        override fun onIceConnectionChange(newState: IceConnectionState) {
            when (newState) {
                IceConnectionState.CONNECTED -> {
                    if (isPublisher && preferredMicEnabled) {
                        soundAmplitudeMeter?.startMeasure()
                    }
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

        }

        override fun onIceConnectionReceivingChange(receiving: Boolean) {

        }

        override fun onAddStream(stream: MediaStream) {
            if (stream.audioTracks.size > 1 || stream.videoTracks.size > 1) {
                return
            }

            if (stream.audioTracks.size == 1) {
                remoteAudioTrack = stream.audioTracks[0]
            }

            if (stream.videoTracks.size == 1) {
                remoteVideoTrack = stream.videoTracks[0]?.apply {
                    setEnabled(true)
                    rendererView?.let {
                        addSink(it)
                    }
                }
            }
        }

        override fun onRemoveStream(stream: MediaStream) {
            remoteVideoTrack = null
            remoteAudioTrack = null
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
                        volumeData[1].toIntOrNull()?.let {
                            events?.onUserSpeaking(it)
                        }
                    }
                })
            }
        }

        override fun onRenegotiationNeeded() {

        }

        override fun onAddTrack(receiver: RtpReceiver, mediaStreams: Array<MediaStream>) {

        }
    }

    /**
     * Local SDP callbacks
     */
    internal inner class LocalSdpObserver : SdpObserver {

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
    internal inner class RemoteSdpObserver : SdpObserver {

        override fun onCreateSuccess(origSdp: SessionDescription) {}

        override fun onSetSuccess() {
            if (!isPublisher && !isScreenShare) {
                peerConnection?.createAnswer(localSdpObserver, sdpMediaConstraints)
            }
        }

        override fun onCreateFailure(error: String) {}

        override fun onSetFailure(error: String) {}
    }

    companion object {
        const val AUDIO_TRACK_ID = "ARDAMSa0"

        internal const val VIDEO_TRACK_ID = "ARDAMSv0"
        internal const val VIDEO_TRACK_TYPE = "video"
        internal const val VIDEO_CODEC_VP8 = "VP8"
        internal const val VIDEO_CODEC_VP9 = "VP9"
        internal const val VIDEO_CODEC_H264 = "H264"
        internal const val VIDEO_CODEC_H264_BASELINE = "H264 Baseline"
        internal const val VIDEO_CODEC_H264_HIGH = "H264 High"
        internal const val AUDIO_CODEC_OPUS = "opus"
        internal const val AUDIO_CODEC_ISAC = "ISAC"
        internal const val VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate"
        internal const val VIDEO_FLEXFEC_FIELDTRIAL =
            "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/"
        internal const val VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/"
        internal const val VIDEO_H264_HIGH_PROFILE_FIELDTRIAL = "WebRTC-H264HighProfile/Enabled/"
        internal const val DISABLE_WEBRTC_AGC_FIELDTRIAL =
            "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/"
        internal const val VIDEO_FRAME_EMIT_FIELDTRIAL: String = "VideoFrameEmit/Enabled/"
        internal const val AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate"
        internal const val AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation"
        internal const val AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl"
        internal const val AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter"
        internal const val AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression"
        internal const val AUDIO_LEVEL_CONTROL_CONSTRAINT = "levelControl"
        internal const val DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement"
        internal const val VIDEO_WIDTH = 720
        internal const val VIDEO_HEIGHT = 480
        internal const val SCREEN_SHARE_VIDEO_WIDTH = 1280
        internal const val SCREEN_SHARE_VIDEO_HEIGHT = 720
        internal const val VIDEO_FPS = 30
        internal const val SCREEN_SHARE_VIDEO_FPS = 60
        internal const val BPS_IN_KBPS = 1000
    }

}
