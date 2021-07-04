package com.kme.kaltura.kmesdk.webrtc.peerconnection.impl

import android.content.Context
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import org.webrtc.DataChannel
import org.webrtc.PeerConnection
import org.webrtc.VideoCapturer

/**
 * An implementation actions under WebRTC peer connection object
 */
class KmeViewerPeerConnectionImpl(
    context: Context,
    events: IKmePeerConnectionEvents
) : KmeBasePeerConnectionImpl(context, events) {

    init {
        isPublisher = false
        isScreenShare = false
    }

    override fun createPeerConnection(
        localRenderer: KmeSurfaceRendererView?,
        remoteRenderer: KmeSurfaceRendererView?,
        videoCapturer: VideoCapturer?,
        useDataChannel: Boolean,
        iceServers: MutableList<PeerConnection.IceServer>
    ) {
        super.createPeerConnection(
            localRenderer,
            remoteRenderer,
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

    override fun addRemoteRenderer(renderer: KmeSurfaceRendererView) {
        remoteVideoTrack?.addSink(renderer)
    }

    override fun removeRemoteRenderer(renderer: KmeSurfaceRendererView) {
        remoteVideoTrack?.removeSink(renderer)
    }

    override fun setAudioEnabled(enable: Boolean) {
        remoteAudioTrack?.setEnabled(enable)
    }

}
