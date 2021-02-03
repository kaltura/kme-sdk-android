package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmePeerConnection
import com.kme.kaltura.kmesdk.controller.room.IKmePeerConnectionModule
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import org.koin.core.inject

/**
 * An implementation for wrap actions with [IKmePeerConnection]
 */
class KmePeerConnectionModuleImpl : KmeController(), IKmePeerConnectionModule {

    private val publisherPeerConnection: IKmePeerConnection by inject()
    private var peerConnections: MutableMap<String, IKmePeerConnection> = mutableMapOf()

    private lateinit var turnUrl: String
    private lateinit var turnUser: String
    private lateinit var turnCred: String

    /**
     * Setting TURN server for RTC
     */
    override fun setTurnServer(
        turnUrl: String,
        turnUser: String,
        turnCred: String
    ) {
        this.turnUrl = turnUrl
        this.turnUser = turnUser
        this.turnCred = turnCred
    }

    /**
     * Creates publisher connection
     */
    override fun addPublisherPeerConnection(
        requestedUserIdStream: String,
        renderer: KmeSurfaceRendererView,
        listener: IKmePeerConnectionClientEvents
    ) : IKmePeerConnection? {
        peerConnections[requestedUserIdStream]?.let { return null }

        publisherPeerConnection.setTurnServer(turnUrl, turnUser, turnCred)
        publisherPeerConnection.setLocalRenderer(renderer)
        publisherPeerConnection.createPeerConnection(true, requestedUserIdStream, listener)
        peerConnections[requestedUserIdStream] = publisherPeerConnection
        return publisherPeerConnection
    }

    /**
     * Creates a viewer connection
     */
    override fun addViewerPeerConnection(
        requestedUserIdStream: String,
        renderer: KmeSurfaceRendererView,
        listener: IKmePeerConnectionClientEvents
    ) : IKmePeerConnection? {
        peerConnections[requestedUserIdStream]?.let { return null }

        val viewerPeerConnection: IKmePeerConnection by inject()
        viewerPeerConnection.setTurnServer(turnUrl, turnUser, turnCred)
        viewerPeerConnection.setRemoteRenderer(renderer)
        viewerPeerConnection.createPeerConnection(false, requestedUserIdStream, listener)
        peerConnections[requestedUserIdStream] = viewerPeerConnection
        return viewerPeerConnection
    }

    /**
     * Getting publisher connection if exist
     */
    override fun getPublisherConnection(): IKmePeerConnection? {
        return publisherPeerConnection
    }

    /**
     * Getting publisher/viewer connection by id
     */
    override fun getPeerConnection(requestedUserIdStream: String): IKmePeerConnection? {
        return peerConnections.getOrDefault(requestedUserIdStream, null)
    }

    /**
     * Disconnect publisher/viewer connection by id
     */
    override fun disconnectPeerConnection(requestedUserIdStream: String) {
        peerConnections[requestedUserIdStream]?.disconnectPeerConnection()
        peerConnections.remove(requestedUserIdStream)
    }

    /**
     * Disconnect all publisher/viewers connections
     */
    override fun disconnectAllConnections() {
        peerConnections.forEach { (_, connection) -> connection.disconnectPeerConnection() }
        peerConnections.clear()
    }

}
