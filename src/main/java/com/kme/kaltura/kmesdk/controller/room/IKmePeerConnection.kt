package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.message.type.KmeSdpType

/**
 * An interface for actions related to p2p connection
 */
interface IKmePeerConnection {

    /**
     * Setting TURN server for RTC
     *
     * @param turnUrl url of a TURN server
     * @param turnUser username for TURN server
     * @param turnCred password for TURN server
     */
    fun setTurnServer(
        turnUrl: String,
        turnUser: String,
        turnCred: String
    )

    /**
     * Setting view for local stream rendering
     *
     * @param localRenderer view to render on
     */
    fun setLocalRenderer(localRenderer: KmeSurfaceRendererView)

    /**
     * Setting view for remote stream rendering
     *
     * @param remoteRenderer view to render on
     */
    fun setRemoteRenderer(remoteRenderer: KmeSurfaceRendererView)

    /**
     * Creates p2p connection
     *
     * @param isPublisher indicates type of connection
     * @param requestedUserIdStream id of a stream
     * @param listener listener for p2p events
     */
    fun createPeerConnection(
        isPublisher: Boolean,
        requestedUserIdStream: String,
        listener: IKmePeerConnectionClientEvents
    )

    /**
     * Setting media server id for data relay
     *
     * @param mediaServerId id of a server
     */
    fun setMediaServerId(mediaServerId: Long)

    /**
     * Creates an offers
     */
    fun createOffer()

    /**
     * Setting remote SDP
     *
     * @param type type of SDP
     * @param sdp string representation of SDP
     */
    fun setRemoteSdp(type: KmeSdpType, sdp: String)

    /**
     * Toggle camera
     *
     * @param isEnable flag to enable/disable camera
     */
    fun enableCamera(isEnable: Boolean)

    /**
     * Toggle audio
     *
     * @param isEnable flag to enable/disable audio
     */
    fun enableAudio(isEnable: Boolean)

    /**
     * Switch between existing cameras
     */
    fun switchCamera()

    /**
     * Closes actual p2p connection
     */
    fun disconnectPeerConnection()

}
