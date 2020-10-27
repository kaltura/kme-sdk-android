package com.kme.kaltura.kmesdk.controller.impl

import com.kme.kaltura.kmesdk.controller.IKmePeerConnectionController
import com.kme.kaltura.kmesdk.controller.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.IKmeWebSocketController
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomInfoResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomsResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetWebRTCServerResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeRoomApiService
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.IKmeWSConnectionListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

class KmeRoomControllerImpl : KmeController(), IKmeRoomController {

    private val roomApiService: KmeRoomApiService by inject()
    private val webSocketController: IKmeWebSocketController by inject()
    private val publisherPeerConnection: IKmePeerConnectionController by inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var peerConnections: MutableMap<Long, IKmePeerConnectionController> = mutableMapOf()
    private lateinit var turnUrl: String
    private lateinit var turnUser: String
    private lateinit var turnCred: String

    override fun getRooms(
        companyId: Long,
        pages: Long,
        limit: Long,
        success: (response: KmeGetRoomsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomApiService.getRooms(companyId, pages, limit) },
                success,
                error
            )
        }
    }

    override fun getRoomInfo(
        alias: String,
        checkPermission: Int,
        withFiles: Int,
        success: (response: KmeGetRoomInfoResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomApiService.getRoomInfo(alias, checkPermission, withFiles) },
                success,
                error
            )
        }
    }

    override fun getWebRTCLiveServer(
        roomAlias: String,
        success: (response: KmeGetWebRTCServerResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomApiService.getWebRTCLiveServer(roomAlias) },
                success,
                error
            )
        }
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

    override fun send(message: KmeMessage<out KmeMessage.Payload>) {
        webSocketController.send(message)
    }

    override fun addListener(listener: IKmeMessageListener) {
        webSocketController.addListener(listener)
    }

    override fun addListener(event: KmeMessageEvent, listener: IKmeMessageListener) {
        webSocketController.addListener(event, listener)
    }

    override fun listen(listener: IKmeMessageListener, vararg events: KmeMessageEvent) {
        webSocketController.listen(listener, *events)
    }

    override fun remove(listener: IKmeMessageListener, vararg events: KmeMessageEvent) {
        webSocketController.remove(listener, *events)
    }

    override fun removeListener(listener: IKmeMessageListener) {
        webSocketController.removeListener(listener)
    }

    override fun removeListeners() {
        webSocketController.removeListeners()
    }

    override fun disconnect() {
        webSocketController.disconnect()
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

    override fun getPublisherConnection(): IKmePeerConnectionController {
        return publisherPeerConnection
    }

    override fun getViewerConnection(userId: Long): IKmePeerConnectionController {
        return peerConnections.getValue(userId)
    }

    override fun disconnectAllConnections() {
        peerConnections.forEach { (_, connection) -> connection.disconnectPeerConnection() }
    }

}
