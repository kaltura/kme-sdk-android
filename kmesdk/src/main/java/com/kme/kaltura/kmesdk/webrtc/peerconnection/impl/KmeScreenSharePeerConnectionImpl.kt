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

    /**
     * Creates peer connection
     */
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

    /**
     * Add renderer for peer connection
     */
    override fun addRenderer(rendererView: KmeSurfaceRendererView) {
        with(rendererView) {
            if (!isInitialized) {
                init(getRenderContext(), null)
                setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                setEnableHardwareScaler(true)
                setMirror(true)
            }

            renderers.add(this)
            localVideoTrack?.addSink(this)
        }
    }

    /**
     * Remove specific renderer for peer connection
     */
    override fun removeRenderer(rendererView: KmeSurfaceRendererView) {
        renderers.find {
            it == rendererView
        }?.let {
            localVideoTrack?.removeSink(it)
            it.release()
            renderers.remove(it)
        }
    }

    /**
     * Remove all connection renderers
     */
    override fun removeRenderers() {
        renderers.forEach {
            localVideoTrack?.removeSink(it)
            it.release()
        }
        renderers.clear()
    }

}
