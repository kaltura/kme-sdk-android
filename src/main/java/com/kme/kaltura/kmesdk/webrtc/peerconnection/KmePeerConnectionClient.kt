package com.kme.kaltura.kmesdk.webrtc.peerconnection

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.kme.kaltura.kmesdk.webrtc.signaling.KmeSignalingParameters
import org.webrtc.*
import org.webrtc.PeerConnection.*
import org.webrtc.voiceengine.WebRtcAudioManager
import org.webrtc.voiceengine.WebRtcAudioRecord
import org.webrtc.voiceengine.WebRtcAudioTrack
import org.webrtc.voiceengine.WebRtcAudioUtils
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Executors
import java.util.regex.Pattern

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

    private val executor = Executors.newSingleThreadExecutor()
    private val sdpObserver: SDPObserver = SDPObserver()
    private val pcObserver: PCObserver = PCObserver()

    private var rootEglBase: EglBase? = null

    private var factory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var options: PeerConnectionFactory.Options? = null
    private lateinit var peerConnectionParameters: KmePeerConnectionParameters
    private var events: IKmePeerConnectionEvents? = null
    private var isInitiator = false

    private var preferredVideoCodec: String? = null

    private var videoCallEnabled: Boolean = false
    private var preferIsac = false
    private var videoCapturerStopped = false
    private var isError = false
    private var queuedRemoteCandidates: MutableList<IceCandidate>? = null
    private lateinit var localSdp: SessionDescription // either offer or answer SDP
    private var mediaStream: MediaStream? = null
    private var videoCapturer: VideoCapturer? = null
    private var renderVideo = false
    private var localVideoTrack: VideoTrack? = null
    private var remoteVideoTrack: VideoTrack? = null
    private var localVideoSender: RtpSender? = null

    private var localRender: VideoSink? = null
    private var remoteRenders: List<VideoRenderer.Callbacks>? = null
    private var signalingParameters: KmeSignalingParameters? = null

    private var audioSource: AudioSource? = null
    private var videoSource: VideoSource? = null

    // enableAudio is set to true if audio should be sent.
    private var enableAudio = false
    private var localAudioTrack: AudioTrack? = null
    private var audioConstraints: MediaConstraints? = null
    private var sdpMediaConstraints: MediaConstraints? = null
    private var dataChannel: DataChannel? = null

    private var videoWidth = 0
    private var videoHeight = 0
    private var videoFps = 0

    init {
        rootEglBase = EglBase.create()
    }

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
        isError = false
        queuedRemoteCandidates = null
//        localSdp = SessionDescription(SessionDescription.Type.OFFER, "") // either offer or answer SDP

        mediaStream = null
        videoCapturer = null
        renderVideo = true
        localVideoTrack = null
        remoteVideoTrack = null
        localVideoSender = null
        enableAudio = true
        localAudioTrack = null

//        executor.execute {
        createPeerConnectionFactoryInternal(context)
//        }
    }

    private fun createPeerConnectionFactoryInternal(context: Context) {
        isError = false

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
//                .setEnableVideoHwAcceleration(peerConnectionParameters!!.videoCodecHwAcceleration)
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

        WebRtcAudioRecord.setErrorCallback(object :
            WebRtcAudioRecord.WebRtcAudioRecordErrorCallback {
            override fun onWebRtcAudioRecordInitError(errorMessage: String) {
                reportError(errorMessage)
            }

            override fun onWebRtcAudioRecordStartError(
                errorCode: WebRtcAudioRecord.AudioRecordStartErrorCode, errorMessage: String
            ) {
                reportError(errorMessage)
            }

            override fun onWebRtcAudioRecordError(errorMessage: String) {
                reportError(errorMessage)
            }
        })

        WebRtcAudioTrack.setErrorCallback(object : WebRtcAudioTrack.ErrorCallback {
            override fun onWebRtcAudioTrackInitError(
                errorMessage: String
            ) {
                reportError(errorMessage)
            }

            override fun onWebRtcAudioTrackStartError(
                errorCode: WebRtcAudioTrack.AudioTrackStartErrorCode, errorMessage: String
            ) {
                reportError(errorMessage)
            }

            override fun onWebRtcAudioTrackError(
                errorMessage: String
            ) {
                reportError(errorMessage)
            }
        })

        val enableH264HighProfile = VIDEO_CODEC_H264_HIGH == peerConnectionParameters.videoCodec
        val encoderFactory: VideoEncoderFactory
        val decoderFactory: VideoDecoderFactory
        if (peerConnectionParameters.videoCodecHwAcceleration) {
            encoderFactory = DefaultVideoEncoderFactory(
                rootEglBase?.eglBaseContext,
                true,
                enableH264HighProfile
            )
            decoderFactory = DefaultVideoDecoderFactory(rootEglBase?.eglBaseContext)
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
//        executor.execute {
//            if (!isError) {
//                events?.onPeerConnectionError(errorMessage)
//                isError = true
//            }
//        }
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

        executor.execute {
            createMediaConstraintsInternal()
            createPeerConnectionInternal()
        }
    }

    private fun createMediaConstraintsInternal() {
        // Check if there is a camera on device and disable video call if not.
        if (videoCapturer == null) {
//            Log.w(PeerConnectionClient.TAG, "No camera on device. Switch to audio only call.")
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
        if (peerConnectionParameters.noAudioProcessing) {
            audioConstraints?.mandatory?.add(
                MediaConstraints.KeyValuePair(
                    AUDIO_ECHO_CANCELLATION_CONSTRAINT,
                    "false"
                )
            )
            audioConstraints?.mandatory?.add(
                MediaConstraints.KeyValuePair(
                    AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT,
                    "false"
                )
            )
            audioConstraints?.mandatory?.add(
                MediaConstraints.KeyValuePair(
                    AUDIO_HIGH_PASS_FILTER_CONSTRAINT,
                    "false"
                )
            )
            audioConstraints?.mandatory?.add(
                MediaConstraints.KeyValuePair(
                    AUDIO_NOISE_SUPPRESSION_CONSTRAINT,
                    "false"
                )
            )
        }

        if (peerConnectionParameters.enableLevelControl) {
            audioConstraints?.mandatory?.add(
                MediaConstraints.KeyValuePair(
                    AUDIO_LEVEL_CONTROL_CONSTRAINT,
                    "true"
                )
            )
        }

        // Create SDP constraints.
        sdpMediaConstraints = MediaConstraints()
        sdpMediaConstraints?.mandatory?.add(
            MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
        )
        if (videoCallEnabled || peerConnectionParameters.loopback) {
            sdpMediaConstraints?.mandatory?.add(
                MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")
            )
        } else {
            sdpMediaConstraints?.mandatory?.add(
                MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false")
            )
        }

        sdpMediaConstraints?.mandatory?.add(
            MediaConstraints.KeyValuePair("RtpDataChannels", "true")
        )

    }

    private fun createPeerConnectionInternal() {
        if (factory == null || isError) {
            return
        }

        queuedRemoteCandidates = ArrayList()
        if (videoCallEnabled) {
            factory?.setVideoHwAccelerationOptions(
                rootEglBase?.eglBaseContext, rootEglBase?.eglBaseContext
            )
        }

        val rtcConfig = RTCConfiguration(signalingParameters?.iceServers)
        peerConnection = factory?.createPeerConnection(rtcConfig, pcObserver)

        val dataChannelInit: DataChannel.Init = DataChannel.Init()
        dataChannelInit.ordered = false
        dataChannelInit.maxRetransmits = 0
        dataChannel = peerConnection?.createDataChannel("volumeDataChannel", dataChannelInit)

        dataChannel?.registerObserver(object : DataChannel.Observer {
            override fun onBufferedAmountChange(previousAmount: Long) { }

            override fun onStateChange() { }

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

        isInitiator = false

        // Set INFO libjingle logging. NOTE: this _must_ happen while |factory| is alive!
        Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO)

        mediaStream = factory?.createLocalMediaStream("ARDAMS")
        if (videoCallEnabled) {
            mediaStream?.addTrack(createVideoTrack(videoCapturer))
        }

        mediaStream?.addTrack(createAudioTrack())
        peerConnection?.addStream(mediaStream)
        if (videoCallEnabled) {
            findVideoSender()
        }

//        if (peerConnectionParameters?.aecDump!!) {
//            try {
//                val aecDumpFileDescriptor = ParcelFileDescriptor.open(
//                    File(
//                        Environment.getExternalStorageDirectory().path
//                                + File.separator + "Download/audio.aecdump"
//                    ),
//                    (ParcelFileDescriptor.MODE_READ_WRITE or ParcelFileDescriptor.MODE_CREATE
//                            or ParcelFileDescriptor.MODE_TRUNCATE)
//                )
//                factory?.startAecDump(aecDumpFileDescriptor.fd, -1)
//            } catch (e: IOException) {
//                Log.e(PeerConnectionClient.TAG, "Can not open aecdump file", e)
//            }
//        }
        events?.onPeerConnectionCreated()
        Log.d(TAG, "Peer connection created.")
    }

    private fun createAudioTrack(): AudioTrack? {
        audioSource = factory?.createAudioSource(audioConstraints)
        localAudioTrack = factory?.createAudioTrack(AUDIO_TRACK_ID, audioSource)
        localAudioTrack?.setEnabled(enableAudio)
        return localAudioTrack
    }

    private fun createVideoTrack(capturer: VideoCapturer?): VideoTrack? {
        videoSource = factory?.createVideoSource(capturer)
        capturer?.startCapture(videoWidth, videoHeight, videoFps)
        localVideoTrack = factory?.createVideoTrack(VIDEO_TRACK_ID, videoSource)
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
        executor.execute {
            enableAudio = enable
            if (localAudioTrack != null) {
                localAudioTrack?.setEnabled(enableAudio)
            }
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    fun setVideoEnabled(enable: Boolean) {
        executor.execute {
            renderVideo = enable
            if (localVideoTrack != null) {
                localVideoTrack?.setEnabled(renderVideo)
            }
            if (remoteVideoTrack != null) {
                remoteVideoTrack?.setEnabled(renderVideo)
            }
        }
    }

    fun createOffer() {
//        executor.execute {
            if (peerConnection != null && !isError) {
                isInitiator = true
                peerConnection?.createOffer(sdpObserver, sdpMediaConstraints)
            }
//        }
    }

    fun createAnswer() {
        executor.execute {
            if (peerConnection != null && !isError) {
                isInitiator = false
                peerConnection?.createAnswer(sdpObserver, sdpMediaConstraints)
            }
        }
    }

    fun addRemoteIceCandidate(candidate: IceCandidate?) {
        executor.execute {
            if (peerConnection != null && !isError) {
                if (queuedRemoteCandidates != null) {
                    queuedRemoteCandidates?.add(candidate!!)
                } else {
                    peerConnection?.addIceCandidate(candidate)
                }
            }
        }
    }

    fun removeRemoteIceCandidates(candidates: Array<IceCandidate>) {
        executor.execute(Runnable {
            if (peerConnection == null || isError) {
                return@Runnable
            }
            drainCandidates()
            peerConnection?.removeIceCandidates(candidates)
        })
    }

    fun setRemoteDescription(sdp: SessionDescription) {
        executor.execute(Runnable {
            if (peerConnection == null || isError) {
                return@Runnable
            }

            var sdpDescription = sdp.description
            if (preferIsac) {
                sdpDescription = preferCodec(
                    sdpDescription,
                    AUDIO_CODEC_ISAC,
                    true
                )
            }
            if (videoCallEnabled) {
                sdpDescription = preferCodec(
                    sdpDescription,
                    preferredVideoCodec,
                    false
                )
            }
            if (peerConnectionParameters.audioStartBitrate > 0) {
                sdpDescription = setStartBitrate(
                    AUDIO_CODEC_OPUS,
                    false,
                    sdpDescription,
                    peerConnectionParameters.audioStartBitrate
                )
            }

            val sdpRemote = SessionDescription(sdp.type, sdpDescription)
            peerConnection!!.setRemoteDescription(sdpObserver, sdpRemote)
        })
    }

    fun stopVideoSource() {
        executor.execute {
            if (videoCapturer != null && !videoCapturerStopped) {
                try {
                    videoCapturer!!.stopCapture()
                } catch (e: InterruptedException) {}
                videoCapturerStopped = true
            }
        }
    }

    fun startVideoSource() {
        executor.execute {
            if (videoCapturer != null && videoCapturerStopped) {
                videoCapturer!!.startCapture(videoWidth, videoHeight, videoFps)
                videoCapturerStopped = false
            }
        }
    }

    fun setVideoMaxBitrate(maxBitrateKbps: Int?) {
        executor.execute(Runnable {
            if (peerConnection == null || localVideoSender == null || isError) {
                return@Runnable
            }
            if (localVideoSender == null) {
                return@Runnable
            }
            val parameters = localVideoSender!!.parameters
            if (parameters.encodings.size == 0) {
                return@Runnable
            }
            for (encoding in parameters.encodings) {
                encoding.maxBitrateBps =
                    if (maxBitrateKbps == null) null else maxBitrateKbps * BPS_IN_KBPS
            }

            if (!localVideoSender!!.setParameters(parameters)) {
                //TODO handle parameters are not set
            }
        })
    }

    private fun setStartBitrate(
        codec: String,
        isVideoCodec: Boolean,
        sdpDescription: String,
        bitrateKbps: Int
    ): String? {
        val lines = sdpDescription.split("\r\n".toRegex()).toTypedArray()
        var rtpmapLineIndex = -1
        var sdpFormatUpdated = false
        var codecRtpMap: String? = null
        // Search for codec rtpmap in format
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        var regex = "^a=rtpmap:(\\d+) $codec(/\\d+)+[\r]?$"
        var codecPattern = Pattern.compile(regex)
        for (i in lines.indices) {
            val codecMatcher = codecPattern.matcher(lines[i])
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1)
                rtpmapLineIndex = i
                break
            }
        }

        if (codecRtpMap == null) {
//            Log.w(
//                org.appspot.apprtc.PeerConnectionClient.TAG,
//                "No rtpmap for $codec codec"
//            )
            return sdpDescription
        }
//        Log.d(
//            org.appspot.apprtc.PeerConnectionClient.TAG,
//            "Found " + codec + " rtpmap " + codecRtpMap + " at " + lines[rtpmapLineIndex]
//        )

        // Check if a=fmtp string already exist in remote SDP for this codec and
        // update it with new bitrate parameter.
        regex = "^a=fmtp:$codecRtpMap \\w+=\\d+.*[\r]?$"
        codecPattern = Pattern.compile(regex)
        for (i in lines.indices) {
            val codecMatcher = codecPattern.matcher(lines[i])
            if (codecMatcher.matches()) {
//                Log.d(
//                    org.appspot.apprtc.PeerConnectionClient.TAG,
//                    "Found " + codec + " " + lines[i]
//                )
                if (isVideoCodec) {
                    lines[i] += "; $VIDEO_CODEC_PARAM_START_BITRATE=$bitrateKbps"
                } else {
                    lines[i] += "; $AUDIO_CODEC_PARAM_BITRATE=$(bitrateKbps * 1000)"
                }
//                Log.d(
//                    org.appspot.apprtc.PeerConnectionClient.TAG,
//                    "Update remote SDP line: " + lines[i]
//                )
                sdpFormatUpdated = true
                break
            }
        }

        val newSdpDescription = java.lang.StringBuilder()
        for (i in lines.indices) {
            newSdpDescription.append(lines[i]).append("\r\n")
            // Append new a=fmtp line if no such line exist for a codec.
            if (!sdpFormatUpdated && i == rtpmapLineIndex) {
                val bitrateSet: String = if (isVideoCodec) {
                    "a=fmtp:$codecRtpMap $VIDEO_CODEC_PARAM_START_BITRATE=$bitrateKbps"
                } else {
                    "a=fmtp:$codecRtpMap $AUDIO_CODEC_PARAM_BITRATE=$(bitrateKbps * 1000)"
                }
//                Log.d(
//                    org.appspot.apprtc.PeerConnectionClient.TAG,
//                    "Add remote SDP line: $bitrateSet"
//                )
                newSdpDescription.append(bitrateSet).append("\r\n")
            }
        }
        return newSdpDescription.toString()
    }

    private fun preferCodec(
        sdpDescription: String,
        codec: String?,
        isAudio: Boolean
    ): String? {
        val lines = sdpDescription.split("\r\n".toRegex()).toTypedArray()
        val mLineIndex: Int = findMediaDescriptionLine(isAudio, lines)
        if (mLineIndex == -1) {
//            Log.w(
//                org.appspot.apprtc.PeerConnectionClient.TAG,
//                "No mediaDescription line, so can't prefer $codec"
//            )
            return sdpDescription
        }
        // A list with all the payload types with name |codec|. The payload types are integers in the
        // range 96-127, but they are stored as strings here.
        val codecPayloadTypes: MutableList<String> = ArrayList()
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        val codecPattern = Pattern.compile("^a=rtpmap:(\\d+) $codec(/\\d+)+[\r]?$")
        for (line in lines) {
            val codecMatcher = codecPattern.matcher(line)
            if (codecMatcher.matches()) {
                codecPayloadTypes.add(codecMatcher.group(1))
            }
        }
        if (codecPayloadTypes.isEmpty()) {
//            Log.w(
//                org.appspot.apprtc.PeerConnectionClient.TAG,
//                "No payload types with name $codec"
//            )
            return sdpDescription
        }
        val newMLine: String = movePayloadTypesToFront(
            codecPayloadTypes,
            lines[mLineIndex]
        ) ?: return sdpDescription
//        Log.d(
//            org.appspot.apprtc.PeerConnectionClient.TAG,
//            "Change media description from: " + lines[mLineIndex] + " to " + newMLine
//        )
        lines[mLineIndex] = newMLine
        return joinString(
            Arrays.asList(*lines),
            "\r\n",
            true
        )
    }

    /** Returns the line number containing "m=audio|video", or -1 if no such line exists.  */
    private fun findMediaDescriptionLine(isAudio: Boolean, sdpLines: Array<String>): Int {
        val mediaDescription = if (isAudio) "m=audio " else "m=video "
        for (i in sdpLines.indices) {
            if (sdpLines[i].startsWith(mediaDescription)) {
                return i
            }
        }
        return -1
    }

    private fun movePayloadTypesToFront(
        preferredPayloadTypes: List<String>,
        mLine: String
    ): String? {
        // The format of the media description line should be: m=<media> <port> <proto> <fmt> ...
        val origLineParts = listOf(
            *mLine
                .split(" ".toRegex())
                .toTypedArray()
        )
        if (origLineParts.size <= 3) {
//            Log.e(
//                org.appspot.apprtc.PeerConnectionClient.TAG,
//                "Wrong SDP media description format: $mLine"
//            )
            return null
        }
        val header: List<String> = origLineParts.subList(0, 3)
        val unpreferredPayloadTypes: MutableList<String> = ArrayList(
            origLineParts.subList(
                3,
                origLineParts.size
            )
        )
        unpreferredPayloadTypes.removeAll(preferredPayloadTypes)
        // Reconstruct the line with |preferredPayloadTypes| moved to the beginning of the payload
        // types.
        val newLineParts: MutableList<String> = ArrayList()
        newLineParts.addAll(header)
        newLineParts.addAll(preferredPayloadTypes)
        newLineParts.addAll(unpreferredPayloadTypes)
        return joinString(
            newLineParts,
            " ",
            false
        )
    }

    private fun joinString(
        s: Iterable<CharSequence?>,
        delimiter: String,
        delimiterAtEnd: Boolean
    ): String? {
        val iter = s.iterator()
        if (!iter.hasNext()) {
            return ""
        }
        val buffer = StringBuilder(iter.next())
        while (iter.hasNext()) {
            buffer.append(delimiter).append(iter.next())
        }
        if (delimiterAtEnd) {
            buffer.append(delimiter)
        }
        return buffer.toString()
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
    fun switchCamera() = executor.execute { switchCameraInternal() }

    private fun switchCameraInternal() {
        if (videoCapturer is CameraVideoCapturer) {
            if (!videoCallEnabled || isError) {
                return  // No video is sent or only one camera is available or error happened.
            }
            val cameraVideoCapturer = videoCapturer as CameraVideoCapturer
            cameraVideoCapturer.switchCamera(null)
        }
    }

    fun changeCaptureFormat(
        width: Int,
        height: Int,
        framerate: Int
    ) {
        executor.execute {
            changeCaptureFormatInternal(
                width,
                height,
                framerate
            )
        }
    }

    private fun changeCaptureFormatInternal(
        width: Int,
        height: Int,
        frameRate: Int
    ) {
        if (!videoCallEnabled || isError || videoCapturer == null) {
            return
        }
        videoSource?.adaptOutputFormat(width, height, frameRate)
    }

    fun close() {
        if (factory != null && peerConnectionParameters.aecDump) {
            factory?.stopAecDump()
        }

//        statsTimer.cancel()
        if (dataChannel != null) {
            dataChannel?.dispose()
            dataChannel = null
        }
        if (peerConnection != null) {
            peerConnection!!.dispose()
            peerConnection = null
        }

        if (audioSource != null) {
            audioSource?.dispose()
            audioSource = null
        }

        if (videoCapturer != null) {
            try {
                videoCapturer?.stopCapture()
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
            videoCapturerStopped = true
            videoCapturer?.dispose()
            videoCapturer = null
        }

        if (videoSource != null) {
            videoSource?.dispose()
            videoSource = null
        }
        localRender = null
        remoteRenders = null

        if (factory != null) {
            factory?.dispose()
            factory = null
        }
        options = null
        rootEglBase?.release()

        events?.onPeerConnectionClosed()
        PeerConnectionFactory.stopInternalTracingCapture()
        PeerConnectionFactory.shutdownInternalTracer()
        events = null
    }

    fun getRenderContext(): EglBase.Context? {
        return rootEglBase!!.eglBaseContext
    }

    private inner class PCObserver : PeerConnection.Observer {

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
            executor.execute(Runnable {
                if (peerConnection == null || isError) {
                    return@Runnable
                }
                if (stream.audioTracks.size > 1 || stream.videoTracks.size > 1) {
                    reportError("Weird-looking stream: $stream")
                    return@Runnable
                }
                if (stream.videoTracks.size == 1) {
                    remoteVideoTrack = stream.videoTracks[0]
                    remoteVideoTrack?.setEnabled(renderVideo)
                    for (remoteRender in remoteRenders!!) {
                        remoteVideoTrack?.addRenderer(VideoRenderer(remoteRender))
                    }
                }
            })
        }

        override fun onRemoveStream(stream: MediaStream) {
            remoteVideoTrack = null
        }

        override fun onDataChannel(dc: DataChannel) {
            dc.registerObserver(object : DataChannel.Observer {
                override fun onBufferedAmountChange(previousAmount: Long) {
//                    Log.d(
//                        PeerConnectionClient.TAG,
//                        "Data channel buffered amount changed: " + dc.label() + ": " + dc.state()
//                    )
                }

                override fun onStateChange() {
//                    Log.d(
//                        PeerConnectionClient.TAG,
//                        "Data channel state changed: " + dc.label() + ": " + dc.state()
//                    )
                }

                override fun onMessage(buffer: DataChannel.Buffer) {
                    if (buffer.binary) {
//                        Log.d(PeerConnectionClient.TAG, "Received binary msg over $dc")
                        return
                    }
                    val data = buffer.data
                    val bytes = ByteArray(data.capacity())
                    data[bytes]
                    val strData = String(bytes, Charset.forName("UTF-8"))
//                    Log.d(PeerConnectionClient.TAG, "Got msg: $strData over $dc")
                }
            })
        }

        override fun onRenegotiationNeeded() {
            val test = ""
            // No need to do anything; AppRTC follows a pre-agreed-upon
            // signaling/negotiation protocol.
        }

        override fun onAddTrack(receiver: RtpReceiver, mediaStreams: Array<MediaStream>) {
            val test = ""
        }
    }

    private inner class SDPObserver : SdpObserver {

        override fun onCreateSuccess(origSdp: SessionDescription) {
            Log.d(TAG, "onSDPCreateSuccess")
//            if (localSdp != null) {
//                reportError("Multiple SDP create.")
//                return
//            }
            var sdpDescription = origSdp.description
            if (preferIsac) {
                sdpDescription = preferCodec(
                    sdpDescription,
                    AUDIO_CODEC_ISAC,
                    true
                )
            }
            if (videoCallEnabled) {
                sdpDescription = preferCodec(
                    sdpDescription,
                    preferredVideoCodec,
                    false
                )
            }
//            https://bugs.chromium.org/p/chromium/issues/detail?id=414395&thanks=414395&ts=1410809385
            val sdp = SessionDescription(origSdp.type, "${sdpDescription.trim()}\r\n")
            localSdp = sdp
//            executor.execute {
                if (peerConnection != null && !isError) {
                    peerConnection?.setLocalDescription(this, sdp)
                }
//            }
        }

        override fun onSetSuccess() {
            Log.d(TAG, "onSDPSetSuccess")
//            executor.execute(Runnable {
                if (peerConnection == null || isError) {
                    return
//                    return@Runnable
                }
                 if (isInitiator) {
                    // For offering peer connection we first create offer and set
                    // local SDP, then after receiving answer set remote SDP.
                    if (peerConnection?.remoteDescription == null) {
                        // We've just set our local SDP so time to send it.
                        events?.onLocalDescription(localSdp)
                    } else {
                        // We've just set remote description, so drain remote
                        // and send local ICE candidates.
//                        Log.d(
//                            org.appspot.apprtc.PeerConnectionClient.TAG,
//                            "Remote SDP set succesfully"
//                        )
                        drainCandidates()
                    }
                } else {
                    // For answering peer connection we set remote SDP and then
                    // create answer and set local SDP.
                    if (peerConnection?.localDescription != null) {
                        // We've just set our local SDP so time to send it, drain
                        // remote and send local ICE candidates.
//                        Log.d(
//                            org.appspot.apprtc.PeerConnectionClient.TAG,
//                            "Local SDP set succesfully"
//                        )
                        events?.onLocalDescription(localSdp)
                        drainCandidates()
                    } else {
                        // We've just set remote SDP - do nothing for now -
                        // answer will be created soon.
//                        Log.d(
//                            org.appspot.apprtc.PeerConnectionClient.TAG,
//                            "Remote SDP set succesfully"
//                        )
                    }
                }
//            })
        }

        override fun onCreateFailure(error: String) {
            Log.e(TAG, "onSDPCreateFailure")
        }

        override fun onSetFailure(error: String) {
            Log.e(TAG, "onSDPSetFailure")
        }
    }

}
