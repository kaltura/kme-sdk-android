package com.kme.kaltura.kmesdk.controller.impl

import android.util.Log
import com.kme.kaltura.kmesdk.controller.*
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomInfoResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomsResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetWebRTCServerResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeWebRTCServer
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeRoomApiService
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.IKmeWSConnectionListener
import com.kme.kaltura.kmesdk.ws.KmeMessageManager
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

class KmeRoomControllerImpl : KmeController(), IKmeRoomController {

    private val roomApiService: KmeRoomApiService by inject()
    private val webSocketController: IKmeWebSocketController by inject()
    private val publisherPeerConnection: IKmePeerConnectionController by inject()
    private val messageManager: KmeMessageManager by inject()
    private val userController: IKmeUserController by inject()
    private val roomSettingsController: IKmeRoomSettingsController by inject()

    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var peerConnections: MutableMap<Long, IKmePeerConnectionController> = mutableMapOf()

    private lateinit var turnUrl: String
    private lateinit var turnUser: String
    private lateinit var turnCred: String

    override var roomSettings: KmeWebRTCServer? = null
        private set

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
                success = {
                    roomSettings = it.data
                    success(it)
                },
                error = {
                    roomSettings = null
                    error(it)
                }
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
        if (isConnected()) {
            Log.e("TAG", "disconnected")
            disconnect()
            disconnectAllConnections()
        }

        messageManager.listen(
            currentParticipantHandler,
            KmeMessageEvent.ROOM_STATE
        )
        roomSettingsController.subscribe()

        webSocketController.connect(url, companyId, roomId, isReconnect, token, listener)
    }

    override fun isConnected(): Boolean = webSocketController.isConnected()

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

    private val currentParticipantHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            if (KmeMessageEvent.ROOM_STATE == message.name) {
                val stateMessage: KmeRoomInitModuleMessage<KmeRoomInitModuleMessage.RoomStatePayload>? =
                    message.toType()
                val participantsList =
                    stateMessage?.payload?.participants?.values?.toMutableList()

                val currentUserId = userController.getCurrentUserInfo()?.id

                userController.currentParticipant =
                    participantsList?.find { kmeParticipant -> kmeParticipant.userId == currentUserId }
            }
        }
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

    override fun getPeerConnection(userId: Long): IKmePeerConnectionController? {
        return peerConnections.getOrDefault(userId, null)
    }

    override fun disconnect() {
        webSocketController.disconnect()
    }

    override fun disconnectAllConnections() {
        peerConnections.forEach { (_, connection) -> connection.disconnectPeerConnection() }
    }

}
