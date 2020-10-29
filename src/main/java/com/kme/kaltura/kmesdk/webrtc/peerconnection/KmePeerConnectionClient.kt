package com.kme.kaltura.kmesdk.webrtc.peerconnection

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import org.webrtc.*
import org.webrtc.PeerConnection.*
import org.webrtc.audio.JavaAudioDeviceModule.builder
import org.webrtc.voiceengine.WebRtcAudioManager
import org.webrtc.voiceengine.WebRtcAudioUtils
import java.nio.charset.Charset
import java.util.*

class KmePeerConnectionClient {

    private val localSdpObserver: LocalSdpObserver = LocalSdpObserver()
    private val remoteSdpObserver: RemoteSdpObserver = RemoteSdpObserver()
    private val pcObserver: PeerConnectionObserver = PeerConnectionObserver()

    private val rootEglBase: EglBase = EglBase.create()

    private var factory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var iceServers: MutableList<IceServer> = mutableListOf()
    private var isPublisher = false
    private var events: IKmePeerConnectionEvents? = null

    private var videoCapturerStopped = false
    private var renderVideo = true

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
    private var dataChannel: DataChannel? = null

    fun createPeerConnectionFactory(
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

    private fun reportError(errorMessage: String) {
        events?.onPeerConnectionError(errorMessage)
    }

    fun createPeerConnection(
        context: Context,
        localVideoSink: VideoSink,
        remoteVideoSink: VideoSink,
        videoCapturer: VideoCapturer?,
        isPublisher: Boolean,
        iceServers: MutableList<IceServer>
    ) {
        this.localVideoSink = localVideoSink
        this.remoteVideoSink = remoteVideoSink
        this.videoCapturer = videoCapturer
        this.isPublisher = isPublisher
        this.iceServers = iceServers

        createMediaConstraints()
        createPeerConnection(context)
    }

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

    private fun createPeerConnection(context: Context) {
        if (factory == null) {
            return
        }

        queuedRemoteCandidates = ArrayList()

        val rtcConfig = RTCConfiguration(iceServers)
        peerConnection = factory?.createPeerConnection(rtcConfig, pcObserver)

        val dataChannelInit: DataChannel.Init = DataChannel.Init()
        dataChannelInit.ordered = false
        dataChannelInit.maxRetransmits = 0
        dataChannel = peerConnection?.createDataChannel("volumeDataChannel", dataChannelInit)

        dataChannel?.registerObserver(object : DataChannel.Observer {
            override fun onBufferedAmountChange(previousAmount: Long) {
                val test = ""
            }

            override fun onStateChange() {
                val test = ""
            }

            override fun onMessage(buffer: DataChannel.Buffer) {
                if (buffer.binary) {
                    return
                }
                val data = buffer.data
                val bytes = ByteArray(data.capacity())
                data[bytes]
                val strData = String(bytes, Charset.forName("UTF-8"))
            }
        })

        // Set INFO libjingle logging. NOTE: this _must_ happen while |factory| is alive!
        Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO)

        val mediaStreamLabels = listOf("ARDAMS")
        if (videoCapturer != null) {
            peerConnection?.addTrack(
                createLocalVideoTrack(context, videoCapturer),
                mediaStreamLabels
            )
        }

        peerConnection?.addTrack(createLocalAudioTrack(), mediaStreamLabels)

        if (videoCapturer != null) {
            findVideoSender()
        }

        events?.onPeerConnectionCreated()
    }

    private fun createLocalAudioTrack(): AudioTrack? {
        localAudioSource = factory?.createAudioSource(audioConstraints)
        localAudioTrack = factory?.createAudioTrack(AUDIO_TRACK_ID, localAudioSource)
        localAudioTrack?.setEnabled(true)
        return localAudioTrack
    }

    private fun createLocalVideoTrack(context: Context, capturer: VideoCapturer?): VideoTrack? {
        capturer?.let {
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", getRenderContext())
            localVideoSource = factory?.createVideoSource(it.isScreencast)
            it.initialize(surfaceTextureHelper, context, localVideoSource?.capturerObserver)
            it.startCapture(VIDEO_WIDTH, VIDEO_HEIGHT, VIDEO_FPS)
        }

        localVideoTrack = factory?.createVideoTrack(VIDEO_TRACK_ID, localVideoSource)
        localVideoTrack?.setEnabled(renderVideo)
        localVideoTrack?.addSink(localVideoSink)
        return localVideoTrack
    }

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

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun setAudioEnabled(enable: Boolean) {
        localAudioTrack?.setEnabled(enable)
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    fun setVideoEnabled(enable: Boolean) {
        renderVideo = enable
        localVideoTrack?.setEnabled(renderVideo)
        remoteVideoTrack?.setEnabled(renderVideo)
    }

    fun createOffer() {
        peerConnection?.createOffer(localSdpObserver, sdpMediaConstraints)
    }

    fun createAnswer() {
        peerConnection?.createAnswer(localSdpObserver, sdpMediaConstraints)
    }

    fun addRemoteIceCandidate(candidate: IceCandidate?) {
        if (queuedRemoteCandidates != null) {
            queuedRemoteCandidates?.add(candidate!!)
        } else {
            peerConnection?.addIceCandidate(candidate)
        }
    }

    fun removeRemoteIceCandidates(candidates: Array<IceCandidate>) {
        if (peerConnection == null) {
            return
        }
        drainCandidates()
        peerConnection?.removeIceCandidates(candidates)
    }

    fun setRemoteDescription(sdp: SessionDescription) {
        peerConnection?.setRemoteDescription(remoteSdpObserver, sdp)
    }

    fun stopVideoSource() {
        if (!videoCapturerStopped) {
            try {
                videoCapturer?.stopCapture()
            } catch (e: InterruptedException) {

            }
            videoCapturerStopped = true
        }
    }

    fun startVideoSource() {
        if (videoCapturerStopped) {
            videoCapturer?.startCapture(VIDEO_WIDTH, VIDEO_HEIGHT, VIDEO_FPS)
            videoCapturerStopped = false
        }
    }

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

    private fun drainCandidates() {
        if (queuedRemoteCandidates != null) {
            for (candidate in queuedRemoteCandidates!!) {
                peerConnection?.addIceCandidate(candidate)
            }
            queuedRemoteCandidates = null
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    fun switchCamera() {
        if (videoCapturer == null) {
            return  // No video is sent or only one camera is available or error happened.
        }
        if (videoCapturer is CameraVideoCapturer) {
            val cameraVideoCapturer = videoCapturer as CameraVideoCapturer
            cameraVideoCapturer.switchCamera(null)
        }
    }

    private fun changeCaptureFormat(
        width: Int,
        height: Int,
        frameRate: Int
    ) {
        if (videoCapturer == null) {
            return
        }
        localVideoSource?.adaptOutputFormat(width, height, frameRate)
    }

    fun close() {
        dataChannel?.dispose()
        dataChannel = null

        peerConnection?.dispose()
        peerConnection = null

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

        // avoid to dispose factory multiple times
        if (isPublisher) {
            factory?.dispose()
            factory = null
        }

        events?.onPeerConnectionClosed()
        PeerConnectionFactory.stopInternalTracingCapture()
        PeerConnectionFactory.shutdownInternalTracer()
        events = null
    }

    fun getRenderContext(): EglBase.Context? {
        return rootEglBase.eglBaseContext
    }

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
                    events?.onIceConnected()
                }
                IceConnectionState.COMPLETED -> {
                    events?.onIceGatheringDone()
                }
                IceConnectionState.DISCONNECTED -> {
                    events?.onIceDisconnected()
                }
                IceConnectionState.FAILED -> {
                    reportError("ICE connection failed.")
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
                reportError("Weird-looking stream: $stream")
                return
            }

            if (stream.videoTracks.size == 1) {
                remoteVideoTrack = stream.videoTracks[0]
                remoteVideoTrack?.setEnabled(renderVideo)
                remoteVideoTrack?.addSink(remoteVideoSink)
            }
        }

        override fun onRemoveStream(stream: MediaStream) {
            remoteVideoTrack = null
        }

        override fun onDataChannel(dc: DataChannel) {
            val test = ""
        }

        override fun onRenegotiationNeeded() {
            val test = ""
        }

        override fun onAddTrack(receiver: RtpReceiver, mediaStreams: Array<MediaStream>) {
            val test = ""
        }
    }

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
        private const val VIDEO_TRACK_ID = "ARDAMSv0"
        private const val AUDIO_TRACK_ID = "ARDAMSa0"
        private const val VIDEO_TRACK_TYPE = "video"
        private const val VIDEO_CODEC_VP8 = "VP8"
        private const val VIDEO_CODEC_VP9 = "VP9"
        private const val VIDEO_CODEC_H264 = "H264"
        private const val VIDEO_CODEC_H264_BASELINE = "H264 Baseline"
        private const val VIDEO_CODEC_H264_HIGH = "H264 High"
        private const val AUDIO_CODEC_OPUS = "opus"
        private const val AUDIO_CODEC_ISAC = "ISAC"
        private const val VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate"
        private const val VIDEO_FLEXFEC_FIELDTRIAL = "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/"
        private const val VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/"
        private const val VIDEO_H264_HIGH_PROFILE_FIELDTRIAL = "WebRTC-H264HighProfile/Enabled/"
        private const val DISABLE_WEBRTC_AGC_FIELDTRIAL = "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/"
        private const val VIDEO_FRAME_EMIT_FIELDTRIAL: String = "VideoFrameEmit/Enabled/"
        private const val AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate"
        private const val AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation"
        private const val AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl"
        private const val AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter"
        private const val AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression"
        private const val AUDIO_LEVEL_CONTROL_CONSTRAINT = "levelControl"
        private const val DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement"
        private const val VIDEO_WIDTH = 1280
        private const val VIDEO_HEIGHT = 720
        private const val VIDEO_FPS = 30
        private const val BPS_IN_KBPS = 1000
    }

}
