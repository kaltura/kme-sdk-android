package com.kme.kaltura.kmesdk.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.kme.kaltura.kmesdk.controller.IKmePeerConnectionController
import com.kme.kaltura.kmesdk.controller.IKmeWebRTCController
import com.kme.kaltura.kmesdk.controller.IKmeWebSocketController
import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.IKmeWSConnectionListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import org.koin.android.ext.android.inject

class KmeRoomService : Service(), KmeKoinComponent, IKmeWebSocketController, IKmeWebRTCController {

    private val binder: IBinder = RoomServiceBinder()

    private val webSocketController: IKmeWebSocketController by inject()
    private val publisherPeerConnection: IKmePeerConnectionController by inject()

    private lateinit var turnUrl: String
    private lateinit var turnUser: String
    private lateinit var turnCred: String

    private var peerConnections: MutableMap<Long, IKmePeerConnectionController> = mutableMapOf()

    override fun onBind(intent: Intent?): IBinder? = binder

    override fun onCreate() {
        super.onCreate()
        val notification: Notification = createRoomNotification(this)
        startForeground(ROOM_NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun connect(
        url: String,
        companyId: Long,
        roomId: Long,
        isReconnect: Boolean,
        token: String,
        listener: IKmeWSConnectionListener
    ) {
        webSocketController.connect(url, companyId, roomId, isReconnect, token, listener)
    }

    override fun isConnected(): Boolean = webSocketController.isConnected()

    override fun send(message: KmeMessage<out KmeMessage.Payload>) {
        webSocketController.send(message)
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

    override fun addPublisherPeerConnection(
        userId: Long,
        renderer: KmeSurfaceRendererView,
        listener: IKmePeerConnectionClientEvents
    ) {
        publisherPeerConnection.setTurnServer(turnUrl, turnUser, turnCred)
        publisherPeerConnection.setLocalRenderer(renderer)
        publisherPeerConnection.createPeerConnection(true, userId, listener)
        peerConnections[userId] = publisherPeerConnection
    }

    override fun addViewerPeerConnection(
        userId: Long,
        renderer: KmeSurfaceRendererView,
        listener: IKmePeerConnectionClientEvents
    ) {
        val viewerPeerConnection: IKmePeerConnectionController by inject()
        viewerPeerConnection.setTurnServer(turnUrl, turnUser, turnCred)
        viewerPeerConnection.setRemoteRenderer(renderer)
        viewerPeerConnection.createPeerConnection(false, userId, listener)
        peerConnections[userId] = viewerPeerConnection
    }

    override fun getPublisherConnection(): IKmePeerConnectionController? {
        return publisherPeerConnection
    }

    override fun getPeerConnection(userId: Long): IKmePeerConnectionController? {
        return peerConnections.getOrDefault(userId, null)
    }

    override fun disconnect() {
        disconnectAllConnections()
        webSocketController.disconnect()
        stopForeground(true)
        stopSelf()
    }

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