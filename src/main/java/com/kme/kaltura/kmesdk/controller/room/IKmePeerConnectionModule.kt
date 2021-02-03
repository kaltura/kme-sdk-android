package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView

/**
 * An interface for wrap actions with [IKmePeerConnection]
 */
interface IKmePeerConnectionModule {

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
     * @return publisher connection object as [IKmePeerConnection]
     */
    fun addPublisherPeerConnection(
        requestedUserIdStream: String,
        renderer: KmeSurfaceRendererView,
        listener: IKmePeerConnectionClientEvents
    ) : IKmePeerConnection?

    /**
     * Creates a viewer connection
     *
     * @param requestedUserIdStream
     * @param renderer view for video rendering
     * @param listener listener for p2p events
     * @return viewer connection object as [IKmePeerConnection]
     */
    fun addViewerPeerConnection(
        requestedUserIdStream: String,
        renderer: KmeSurfaceRendererView,
        listener: IKmePeerConnectionClientEvents
    ) : IKmePeerConnection?

    /**
     * Getting publisher connection if exist
     *
     * @return publisher connection object as [IKmePeerConnection]
     */
    fun getPublisherConnection() : IKmePeerConnection?

    /**
     * Getting publisher/viewer connection by id
     *
     * @param requestedUserIdStream id of a stream
     * @return publisher/viewer connection object as [IKmePeerConnection]
     */
    fun getPeerConnection(requestedUserIdStream: String) : IKmePeerConnection?

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
