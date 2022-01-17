package com.kme.kaltura.kmesdk.webrtc.peerconnection.impl

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionEvents
import com.kme.kaltura.kmesdk.webrtc.stats.KmeSoundAmplitudeListener
import com.kme.kaltura.kmesdk.webrtc.stats.KmeSoundAmplitudeMeter
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import org.webrtc.*
import java.nio.ByteBuffer

/**
 * An implementation actions under WebRTC peer connection object
 */
class KmePublisherPeerConnectionImpl(
    context: Context,
    events: IKmePeerConnectionEvents
) : KmeBasePeerConnectionImpl(context, events), KmeSoundAmplitudeListener {

    init {
        isPublisher = true
        isScreenShare = false
    }

    override fun setPreferredSettings(
        preferredMicEnabled: Boolean,
        preferredCamEnabled: Boolean
    ) {
        this.preferredMicEnabled = preferredMicEnabled
        this.preferredCamEnabled = preferredCamEnabled
    }

    override fun createPeerConnection(
        videoCapturer: VideoCapturer?,
        useDataChannel: Boolean,
        iceServers: MutableList<PeerConnection.IceServer>
    ) {
        super.createPeerConnection(
            videoCapturer,
            useDataChannel,
            iceServers
        )

        peerConnection?.let { peerConnection ->
            videoCapturer?.let {
                peerConnection.addTrack(createLocalVideoTrack(videoCapturer), videoStreamId)
                findVideoSender()
            }

            if (useDataChannel) {
                val volumeInit = DataChannel.Init()
                volumeInit.ordered = false
                volumeInit.maxRetransmits = 0
                volumeDataChannel =
                    peerConnection.createDataChannel("volumeDataChannel", volumeInit)
            }
            soundAmplitudeMeter = KmeSoundAmplitudeMeter(peerConnection, this)
        }

        events?.onPeerConnectionCreated()
    }

    override fun setRenderer(rendererView: KmeSurfaceRendererView) {
        removeRenderer()

        with(rendererView) {
            if (!isInitialized) {
                init(getRenderContext(), null)
                setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                setEnableHardwareScaler(true)
                setMirror(true)
            }
        }

        this.rendererView = rendererView
        localVideoTrack?.addSink(rendererView)
    }

    override fun removeRenderer() {
        this.rendererView?.let {
            localVideoTrack?.removeSink(it)
            it.release()
            this.rendererView = null
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun setAudioEnabledInternal(enable: Boolean) {
        if (enable) {
            if (preferredMicEnabled) {
                soundAmplitudeMeter?.startMeasure()
                localAudioTrack?.setEnabled(true)
            }
        } else {
            soundAmplitudeMeter?.stopMeasure()
            localAudioTrack?.setEnabled(false)
        }
    }

    /**
     * Toggle audio
     */
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun setAudioEnabled(enable: Boolean) {
        preferredMicEnabled = enable
        if (enable) {
            soundAmplitudeMeter?.startMeasure()
        } else {
            soundAmplitudeMeter?.stopMeasure()
        }
        localAudioTrack?.setEnabled(enable)
    }

    /**
     * Toggle video
     */
    @RequiresPermission(Manifest.permission.CAMERA)
    override fun setVideoEnabled(enable: Boolean) {
        enableVideoSource(enable)
//        localVideoTrack?.setEnabled(enable)
    }

    /**
     * Enable/Disable outgoing video stream
     */
    override fun enableVideoSource(enable: Boolean) {
        if (enable) {
            if (!videoCapturerEnabled) {
                videoCapturer?.startCapture(
                    VIDEO_WIDTH,
                    VIDEO_HEIGHT,
                    VIDEO_FPS
                )
                videoCapturerEnabled = true
            }
        } else if (videoCapturerEnabled) {
            try {
                videoCapturer?.stopCapture()
            } catch (e: InterruptedException) {
            }
            videoCapturerEnabled = false
        }
    }

    /**
     * Switch between existing cameras
     */
    @RequiresPermission(Manifest.permission.CAMERA)
    override fun switchCamera(frontCamera: Boolean) {
        rendererView?.setMirror(frontCamera)
        videoCapturer?.let {
            if (it is CameraVideoCapturer) {
                it.switchCamera(null)
            }
        }
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

}
