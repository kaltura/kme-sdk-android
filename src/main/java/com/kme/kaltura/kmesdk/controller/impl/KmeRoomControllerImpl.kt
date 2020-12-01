package com.kme.kaltura.kmesdk.controller.impl

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_ADJUST_WITH_ACTIVITY
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.kme.kaltura.kmesdk.controller.IKmePeerConnectionController
import com.kme.kaltura.kmesdk.controller.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.IKmeRoomSettingsController
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.*
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomInfoResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomsResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetWebRTCServerResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeWebRTCServer
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeRoomApiService
import com.kme.kaltura.kmesdk.service.RoomService
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


class KmeRoomControllerImpl(
    private val context: Context
) : KmeController(), IKmeRoomController {

    private val roomApiService: KmeRoomApiService by inject()
    private val messageManager: KmeMessageManager by inject()
    private val userController: IKmeUserController by inject()
    private val roomSettingsController: IKmeRoomSettingsController by inject()

    private val uiScope = CoroutineScope(Dispatchers.Main)

    override var roomSettings: KmeWebRTCServer? = null
        private set

    private var roomService: RoomService? = null

    private var companyId: Long = 0
    private var roomId: Long = 0
    private var isReconnect: Boolean = true

    private lateinit var turnUrl: String
    private lateinit var turnUser: String
    private lateinit var turnCred: String

    private lateinit var url: String
    private lateinit var token: String
    private lateinit var listener: IKmeWSConnectionListener

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder: RoomService.RoomServiceBinder = service as RoomService.RoomServiceBinder
            roomService = binder.service
            roomService?.setTurnServer(turnUrl, turnUser, turnCred)
            roomService?.connect(url, companyId, roomId, isReconnect, token, listener)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            roomService = null
        }
    }

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
        startService(url, companyId, roomId, isReconnect, token, listener)
    }

    private fun startService(
        url: String,
        companyId: Long,
        roomId: Long,
        isReconnect: Boolean,
        token: String,
        listener: IKmeWSConnectionListener
    ) {
        this.url = url
        this.companyId = companyId
        this.roomId = roomId
        this.isReconnect = isReconnect
        this.token = token

        this.listener = object : IKmeWSConnectionListener {
            override fun onOpen() {

                messageManager.listen(
                    currentParticipantHandler,
                    KmeMessageEvent.ROOM_STATE
                )

                roomSettingsController.subscribe()

                listener.onOpen()
            }

            override fun onFailure(throwable: Throwable) {
                listener.onFailure(throwable)
            }

            override fun onClosing(code: Int, reason: String) {
                listener.onClosing(code, reason)
            }

            override fun onClosed(code: Int, reason: String) {
                context.unbindService(serviceConnection)
                listener.onClosed(code, reason)
            }
        }

        val intent = Intent(context, RoomService::class.java)
        context.bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    private val currentParticipantHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            if (KmeMessageEvent.ROOM_STATE == message.name) {
                val stateMessage: KmeRoomInitModuleMessage<KmeRoomInitModuleMessage.RoomStatePayload>? =
                    message.toType()
                val participantsList =
                    stateMessage?.payload?.participants?.values?.toMutableList()

                val currentUserId = userController.getCurrentUserInfo()?.getUserId()

                val currentParticipant =
                    participantsList?.find { kmeParticipant -> kmeParticipant.userId == currentUserId }

                currentParticipant?.userPermissions = roomSettings?.roomInfo?.settingsV2

                userController.updateParticipant(currentParticipant)
            }
        }
    }

    override fun isConnected(): Boolean = roomService?.isConnected() ?: false

    override fun send(message: KmeMessage<out KmeMessage.Payload>) {
        roomService?.send(message)
    }

    override fun addListener(listener: IKmeMessageListener) {
        roomService?.addListener(listener)
    }

    override fun addListener(event: KmeMessageEvent, listener: IKmeMessageListener) {
        roomService?.addListener(event, listener)
    }

    override fun listen(listener: IKmeMessageListener, vararg events: KmeMessageEvent) {
        roomService?.listen(listener, *events)
    }

    override fun remove(listener: IKmeMessageListener, vararg events: KmeMessageEvent) {
        roomService?.remove(listener, *events)
    }

    override fun removeListener(listener: IKmeMessageListener) {
        roomService?.removeListener(listener)
    }

    override fun removeListeners() {
        roomService?.removeListeners()
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
        roomService?.addPublisherPeerConnection(userId, renderer, listener)
    }

    override fun addViewerPeerConnection(
        userId: Long,
        renderer: KmeSurfaceRendererView,
        listener: IKmePeerConnectionClientEvents
    ) {
        roomService?.addViewerPeerConnection(userId, renderer, listener)
    }

    override fun getPublisherConnection(): IKmePeerConnectionController? {
        return roomService?.getPublisherConnection()
    }

    override fun getPeerConnection(userId: Long): IKmePeerConnectionController? {
        return roomService?.getPeerConnection(userId)
    }

    override fun disconnect() {
        roomService?.disconnect()
    }

    override fun disconnectAllConnections() {
        roomService?.disconnectAllConnections()
    }

}
