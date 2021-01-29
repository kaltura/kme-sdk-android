package com.kme.kaltura.kmesdk.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.kme.kaltura.kmesdk.controller.room.IKmePeerConnectionModule
import com.kme.kaltura.kmesdk.controller.room.IKmeWebRTCModule
import com.kme.kaltura.kmesdk.controller.room.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.IKmeWSConnectionListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import org.koin.android.ext.android.inject

/**
 * Service wrapper under the room actions
 */
class KmeRoomService : Service(), KmeKoinComponent, IKmeWebSocketModule, IKmeWebRTCModule {

    private val binder: IBinder = RoomServiceBinder()

    private val webSocketModule: IKmeWebSocketModule by inject()
    private val publisherPeerConnection: IKmePeerConnectionModule by inject()

    private lateinit var turnUrl: String
    private lateinit var turnUser: String
    private lateinit var turnCred: String

    private var peerConnections: MutableMap<String, IKmePeerConnectionModule> = mutableMapOf()

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        val notification: Notification = createRoomNotification(this)
        startForeground(ROOM_NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    /**
     * Establish socket connection
     */
    override fun connect(
        url: String,
        companyId: Long,
        roomId: Long,
        isReconnect: Boolean,
        token: String,
        listener: IKmeWSConnectionListener
    ) {
        webSocketModule.connect(url, companyId, roomId, isReconnect, token, listener)
    }

    /**
     * Check is socket connected
     */
    override fun isConnected(): Boolean = webSocketModule.isConnected()

    /**
     * Send message via socket
     */
    override fun send(message: KmeMessage<out KmeMessage.Payload>) {
        webSocketModule.send(message)
    }

    /**
     * Disconnect from the room. Destroy all related connections
     */
    override fun disconnect() {
        disconnectAllConnections()
        webSocketModule.disconnect()
        stopForeground(true)
        stopSelf()
    }

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
    ) : IKmePeerConnectionModule? {
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
    ) : IKmePeerConnectionModule? {
        peerConnections[requestedUserIdStream]?.let { return null }

        val viewerPeerConnection: IKmePeerConnectionModule by inject()
        viewerPeerConnection.setTurnServer(turnUrl, turnUser, turnCred)
        viewerPeerConnection.setRemoteRenderer(renderer)
        viewerPeerConnection.createPeerConnection(false, requestedUserIdStream, listener)
        peerConnections[requestedUserIdStream] = viewerPeerConnection
        return viewerPeerConnection
    }

    /**
     * Getting publisher connection if exist
     */
    override fun getPublisherConnection(): IKmePeerConnectionModule? {
        return publisherPeerConnection
    }

    /**
     * Getting publisher/viewer connection by id
     */
    override fun getPeerConnection(requestedUserIdStream: String): IKmePeerConnectionModule? {
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

    override fun onUnbind(intent: Intent?): Boolean {
        disconnect()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        disconnect()
        super.onDestroy()
    }

    inner class RoomServiceBinder : Binder() {
        val service: KmeRoomService
            get() = this@KmeRoomService
    }

}