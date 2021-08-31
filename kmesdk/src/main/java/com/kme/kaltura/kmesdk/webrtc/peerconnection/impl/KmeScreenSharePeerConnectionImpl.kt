package com.kme.kaltura.kmesdk.webrtc.peerconnection.impl

import android.content.Context
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import org.webrtc.PeerConnection
import org.webrtc.RendererCommon
import org.webrtc.VideoCapturer

/**
 * An implementation actions under WebRTC peer connection object
 */
class KmeScreenSharePeerConnectionImpl(
    context: Context,
    events: IKmePeerConnectionEvents
) : KmeBasePeerConnectionImpl(context, events) {

    init {
        isPublisher = true
        isScreenShare = true
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

}
