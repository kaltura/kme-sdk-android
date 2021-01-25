package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView

/**
 * An interface for wrap actions with [IKmePeerConnectionController]
 */
interface IKmeWebRTCController {

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
     * Creates publisher connection
     *
     * @param requestedUserIdStream id of a stream
     * @param renderer view for video rendering
     * @param listener listener for p2p events
     * @return publisher connection object as [IKmePeerConnectionController]
     */
    fun addPublisherPeerConnection(
        requestedUserIdStream: String,
        renderer: KmeSurfaceRendererView,
        listener: IKmePeerConnectionClientEvents
    ) : IKmePeerConnectionController?

    /**
     * Creates a viewer connection
     *
     * @param requestedUserIdStream
     * @param renderer view for video rendering
     * @param listener listener for p2p events
     * @return viewer connection object as [IKmePeerConnectionController]
     */
    fun addViewerPeerConnection(
        requestedUserIdStream: String,
        renderer: KmeSurfaceRendererView,
        listener: IKmePeerConnectionClientEvents
    ) : IKmePeerConnectionController?

    /**
     * Getting publisher connection if exist
     *
     * @return publisher connection object as [IKmePeerConnectionController]
     */
    fun getPublisherConnection() : IKmePeerConnectionController?

    /**
     * Getting publisher/viewer connection by id
     *
     * @param requestedUserIdStream id of a stream
     * @return publisher/viewer connection object as [IKmePeerConnectionController]
     */
    fun getPeerConnection(requestedUserIdStream: String) : IKmePeerConnectionController?

    /**
     * Disconnect publisher/viewer connection by id
     *
     * @param requestedUserIdStream id of a stream
     */
    fun disconnectPeerConnection(requestedUserIdStream: String)

    /**
     * Disconnect all publisher/viewers connections
     */
    fun disconnectAllConnections()

}
