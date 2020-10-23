package com.kme.kaltura.kmesdk.webrtc.peerconnection

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.kme.kaltura.kmesdk.webrtc.signaling.KmeSignalingParameters
import org.webrtc.*
import org.webrtc.PeerConnection.*
import org.webrtc.voiceengine.WebRtcAudioManager
import org.webrtc.voiceengine.WebRtcAudioUtils
import java.nio.charset.Charset
import java.util.*

class KmePeerConnectionClient {

    private val TAG = KmePeerConnectionClient::class.java.canonicalName

    private val VIDEO_TRACK_ID = "ARDAMSv0"
    private val AUDIO_TRACK_ID = "ARDAMSa0"
    private val VIDEO_TRACK_TYPE = "video"
    private val VIDEO_CODEC_VP8 = "VP8"
    private val VIDEO_CODEC_VP9 = "VP9"
    private val VIDEO_CODEC_H264 = "H264"
    private val VIDEO_CODEC_H264_BASELINE = "H264 Baseline"
    private val VIDEO_CODEC_H264_HIGH = "H264 High"
    private val AUDIO_CODEC_OPUS = "opus"
    private val AUDIO_CODEC_ISAC = "ISAC"
    private val VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate"
    private val VIDEO_FLEXFEC_FIELDTRIAL = "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/"
    private val VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/"
    private val VIDEO_H264_HIGH_PROFILE_FIELDTRIAL = "WebRTC-H264HighProfile/Enabled/"
    private val DISABLE_WEBRTC_AGC_FIELDTRIAL = "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/"
    private val VIDEO_FRAME_EMIT_FIELDTRIAL: String = "VideoFrameEmit/Enabled/"
    private val AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate"
    private val AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation"
    private val AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl"
    private val AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter"
    private val AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression"
    private val AUDIO_LEVEL_CONTROL_CONSTRAINT = "levelControl"
    private val DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement"
    private val HD_VIDEO_WIDTH = 1280
    private val HD_VIDEO_HEIGHT = 720
    private val BPS_IN_KBPS = 1000

    private val localSdpObserver: LocalSdpObserver = LocalSdpObserver()
    private val remoteSdpObserver: RemoteSdpObserver = RemoteSdpObserver()
    private val pcObserver: PeerConnectionObserver = PeerConnectionObserver()

    private val rootEglBase: EglBase = EglBase.create()

    private var factory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var options: PeerConnectionFactory.Options? = null
    private lateinit var peerConnectionParameters: KmePeerConnectionParameters
    private var events: IKmePeerConnectionEvents? = null

    private var preferredVideoCodec: String? = null

    private var videoCallEnabled: Boolean = false
    private var preferIsac = false
    private var videoCapturerStopped = false
    private var queuedRemoteCandidates: MutableList<IceCandidate>? = null
    private lateinit var localSdp: SessionDescription

    private var localMediaStream: MediaStream? = null
    private var videoCapturer: VideoCapturer? = null
    private var renderVideo = false
    private var localVideoTrack: VideoTrack? = null
    private var remoteVideoTrack: VideoTrack? = null
    private var localVideoSender: RtpSender? = null

    private var localRender: VideoSink? = null
    private var remoteRenders: List<VideoRenderer.Callbacks>? = null
    private var signalingParameters: KmeSignalingParameters? = null

    private var localAudioSource: AudioSource? = null
    private var localVideoSource: VideoSource? = null

    private var localAudioTrack: AudioTrack? = null
    private var audioConstraints: MediaConstraints? = null
    private var sdpMediaConstraints: MediaConstraints? = null
    private var dataChannel: DataChannel? = null

    private var videoWidth = 0
    private var videoHeight = 0
    private var videoFps = 0

    fun createPeerConnectionFactory(
        context: Context,
        peerConnectionParameters: KmePeerConnectionParameters,
        events: IKmePeerConnectionEvents
    ) {
        this.peerConnectionParameters = peerConnectionParameters
        this.events = events

        videoCallEnabled = peerConnectionParameters.videoCallEnabled

        factory = null
        peerConnection = null
        preferIsac = false
        videoCapturerStopped = false
        queuedRemoteCandidates = null

        localMediaStream = null
        videoCapturer = null
        renderVideo = true
        localVideoTrack = null
        remoteVideoTrack = null
        localVideoSender = null
        localAudioTrack = null

        createPeerConnectionFactoryInternal(context)
    }

    private fun createPeerConnectionFactoryInternal(context: Context) {
        var fieldTrials = ""
        if (peerConnectionParameters.videoFlexfecEnabled) {
            fieldTrials = "$fieldTrials $VIDEO_FLEXFEC_FIELDTRIAL"
        }
        fieldTrials = "$fieldTrials $VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL"

        if (peerConnectionParameters.disableWebRtcAGCAndHPF) {
            fieldTrials = "$fieldTrials $DISABLE_WEBRTC_AGC_FIELDTRIAL"
        }
        fieldTrials = "$fieldTrials $VIDEO_FRAME_EMIT_FIELDTRIAL"

        if (videoCallEnabled) {
            preferredVideoCodec = when (peerConnectionParameters.videoCodec) {
                VIDEO_CODEC_VP8 -> VIDEO_CODEC_VP8
                VIDEO_CODEC_VP9 -> VIDEO_CODEC_VP9
                VIDEO_CODEC_H264_BASELINE -> VIDEO_CODEC_H264
                VIDEO_CODEC_H264_HIGH -> {
                    // TODO(magjed): Strip High from SDP when selecting Baseline instead of using field trial.
                    fieldTrials = "$fieldTrials $VIDEO_H264_HIGH_PROFILE_FIELDTRIAL"
                    VIDEO_CODEC_H264
                }
                else -> VIDEO_CODEC_VP8
            }
        }

        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setFieldTrials(fieldTrials)
                .setEnableVideoHwAcceleration(true)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )

        // Check if ISAC is used by default.
        preferIsac = peerConnectionParameters.audioCodec == AUDIO_CODEC_ISAC

        if (!peerConnectionParameters.useOpenSLES) {
            WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(true)
        } else {
            WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(false)
        }
        if (peerConnectionParameters.disableBuiltInAEC) {
            WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true)
        } else {
            WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(false)
        }
        if (peerConnectionParameters.disableBuiltInAGC) {
            WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true)
        } else {
            WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(false)
        }
        if (peerConnectionParameters.disableBuiltInNS) {
            WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true)
        } else {
            WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(false)
        }

        val enableH264HighProfile = VIDEO_CODEC_H264_HIGH == peerConnectionParameters.videoCodec
        val encoderFactory: VideoEncoderFactory
        val decoderFactory: VideoDecoderFactory
        if (peerConnectionParameters.videoCodecHwAcceleration) {
            encoderFactory = DefaultVideoEncoderFactory(
                getRenderContext(),
                true,
                enableH264HighProfile
            )
            decoderFactory = DefaultVideoDecoderFactory(getRenderContext())
        } else {
            encoderFactory = SoftwareVideoEncoderFactory()
            decoderFactory = SoftwareVideoDecoderFactory()
        }

        factory = PeerConnectionFactory.builder()
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()
    }

    private fun reportError(errorMessage: String) {
        events?.onPeerConnectionError(errorMessage)
    }

    fun createPeerConnection(
        localRender: VideoSink,
        remoteRenders: List<VideoRenderer.Callbacks>?,
        videoCapturer: VideoCapturer?,
        signalingParameters: KmeSignalingParameters?
    ) {
        this.localRender = localRender
        this.remoteRenders = remoteRenders
        this.videoCapturer = videoCapturer
        this.signalingParameters = signalingParameters

        createMediaConstraintsInternal()
        createPeerConnectionInternal()
    }

    private fun createMediaConstraintsInternal() {
        // Check if there is a camera on device and disable video call if not.
        if (videoCapturer == null) {
            videoCallEnabled = false
        }

        if (videoCallEnabled) {
            videoWidth = peerConnectionParameters.videoWidth
            videoHeight = peerConnectionParameters.videoHeight
            videoFps = peerConnectionParameters.videoFps

            if (videoWidth == 0 || videoHeight == 0) {
                videoWidth = HD_VIDEO_WIDTH
                videoHeight = HD_VIDEO_HEIGHT
            }

            if (videoFps == 0) {
                videoFps = 30
            }
        }

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

        // Create SDP constraints.
        sdpMediaConstraints = MediaConstraints()
        sdpMediaConstraints?.mandatory?.add(
            MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
        )
        sdpMediaConstraints?.mandatory?.add(
            MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")
        )
    }

    private fun createPeerConnectionInternal() {
        if (factory == null) {
            return
        }

        queuedRemoteCandidates = ArrayList()
        if (videoCallEnabled) {
            factory?.setVideoHwAccelerationOptions(getRenderContext(), getRenderContext())
        }

        val rtcConfig = RTCConfiguration(signalingParameters?.iceServers)
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

        localMediaStream = factory?.createLocalMediaStream("ARDAMS")
        if (videoCallEnabled) {
            localMediaStream?.addTrack(createLocalVideoTrack(videoCapturer))
        }
        localMediaStream?.addTrack(createLocalAudioTrack())
        peerConnection?.addStream(localMediaStream)

        if (videoCallEnabled) {
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

    private fun createLocalVideoTrack(capturer: VideoCapturer?): VideoTrack? {
        localVideoSource = factory?.createVideoSource(capturer)
        capturer?.startCapture(videoWidth, videoHeight, videoFps)
        localVideoTrack = factory?.createVideoTrack(VIDEO_TRACK_ID, localVideoSource)
        localVideoTrack?.setEnabled(renderVideo)
        localVideoTrack?.addSink(localRender)
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
            videoCapturer?.startCapture(videoWidth, videoHeight, videoFps)
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
        if (videoCapturer is CameraVideoCapturer) {
            if (!videoCallEnabled) {
                return  // No video is sent or only one camera is available or error happened.
            }
            val cameraVideoCapturer = videoCapturer as CameraVideoCapturer
            cameraVideoCapturer.switchCamera(null)
        }
    }

    private fun changeCaptureFormat(
        width: Int,
        height: Int,
        frameRate: Int
    ) {
        if (!videoCallEnabled || videoCapturer == null) {
            return
        }
        localVideoSource?.adaptOutputFormat(width, height, frameRate)
    }

    fun close() {
        if (peerConnectionParameters.aecDump) {
            factory?.stopAecDump()
        }

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

        localRender = null
        remoteRenders = null

        factory?.dispose()
        factory = null

        options = null

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
                for (remoteRender in remoteRenders!!) {
                    remoteVideoTrack?.addRenderer(VideoRenderer(remoteRender))
                }
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
            Log.d(TAG, "LocalSdpObserver onSDPCreateSuccess")
            localSdp = sdp
            peerConnection?.setLocalDescription(this, sdp)
        }

        override fun onSetSuccess() {
            Log.d(TAG, "LocalSdpObserver onSDPSetSuccess")
            events?.onLocalDescription(localSdp)
        }

        override fun onCreateFailure(error: String) {
            Log.e(TAG, "LocalSdpObserver onSDPCreateFailure")
        }

        override fun onSetFailure(error: String) {
            Log.e(TAG, "LocalSdpObserver onSDPSetFailure")
        }
    }

    private inner class RemoteSdpObserver : SdpObserver {

        override fun onCreateSuccess(origSdp: SessionDescription) {
            Log.d(TAG, "RemoteSdpObserver onCreateSuccess")
        }

        override fun onSetSuccess() {
            Log.d(TAG, "RemoteSdpObserver onSDPSetSuccess")
            peerConnection?.createAnswer(localSdpObserver, sdpMediaConstraints)
        }

        override fun onCreateFailure(error: String) {
            Log.e(TAG, "RemoteSdpObserver onSDPCreateFailure")
        }

        override fun onSetFailure(error: String) {
            Log.e(TAG, "RemoteSdpObserver onSDPSetFailure")
        }
    }

}
