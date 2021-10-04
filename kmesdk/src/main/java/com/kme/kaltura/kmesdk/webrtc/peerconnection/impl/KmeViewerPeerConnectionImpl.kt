package com.kme.kaltura.kmesdk.webrtc.peerconnection.impl

import android.content.Context
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import org.webrtc.DataChannel
import org.webrtc.PeerConnection
import org.webrtc.RendererCommon
import org.webrtc.VideoCapturer

/**
 * An implementation actions under WebRTC peer connection object
 */
class KmeViewerPeerConnectionImpl(
    context: Context,
    events: IKmePeerConnectionEvents,
) : KmeBasePeerConnectionImpl(context, events) {

    init {
        isPublisher = false
        isScreenShare = false
    }

    override fun createPeerConnection(
        videoCapturer: VideoCapturer?,
        useDataChannel: Boolean,
        iceServers: MutableList<PeerConnection.IceServer>,
    ) {
        super.createPeerConnection(
            videoCapturer,
            useDataChannel,
            iceServers
        )

        if (useDataChannel) {
            peerConnection?.let {
                val volumeInit = DataChannel.Init()
                volumeInit.ordered = false
                volumeInit.maxRetransmits = 0
                volumeDataChannel = it.createDataChannel("volumeDataChannel", volumeInit)
            }
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
                setMirror(false)
            }
        }

        this.rendererView = rendererView
        remoteVideoTrack?.addSink(rendererView)
    }

    override fun removeRenderer() {
        this.rendererView?.let {
            remoteVideoTrack?.removeSink(it)
            it.release()
            this.rendererView = null
        }
    }

    override fun setAudioEnabled(enable: Boolean) {
        remoteAudioTrack?.setEnabled(enable)
    }

}
