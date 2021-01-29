package com.kme.kaltura.kmesdk.controller.room.impl

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_ADJUST_WITH_ACTIVITY
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.*
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomInfoResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomsResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetWebRTCServerResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeWebRTCServer
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeRoomApiService
import com.kme.kaltura.kmesdk.service.KmeRoomService
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

/**
 * An implementation for room data
 */
class KmeRoomControllerImpl(
    private val context: Context
) : KmeController(), IKmeRoomController {

    private val roomApiService: KmeRoomApiService by inject()
    private val messageManager: KmeMessageManager by inject()
    private val userController: IKmeUserController by inject()
    private val settingsModule: IKmeSettingsModule by inject()

    override val chatModule: IKmeChatModule by inject()
    override val noteModule: IKmeNoteModule by inject()
    override val recordingModule: IKmeRecordingModule by inject()
    override val desktopShareModule: IKmeDesktopShareModule by inject()
    override val audioModule: IKmeAudioModule by inject()

    private val uiScope = CoroutineScope(Dispatchers.Main)

    override var roomSettings: KmeWebRTCServer? = null
        private set

    private var roomService: KmeRoomService? = null

    private var companyId: Long = 0
    private var roomId: Long = 0
    private var isReconnect: Boolean = true

    private lateinit var turnUrl: String
    private lateinit var turnUser: String
    private lateinit var turnCred: String

    private lateinit var url: String
    private lateinit var token: String
    private lateinit var listener: IKmeWSConnectionListener

    /**
     * ServiceConnection block to pass data related to the room
     */
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder: KmeRoomService.RoomServiceBinder = service as KmeRoomService.RoomServiceBinder
            roomService = binder.service
            roomService?.setTurnServer(turnUrl, turnUser, turnCred)
            roomService?.connect(url, companyId, roomId, isReconnect, token, listener)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            roomService = null
        }
    }

    /**
     * Getting all rooms for specific company
     */
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

    /**
     * Getting room info by alias
     */
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

    /**
     * Getting data for p2p connection
     */
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
        startService(url, companyId, roomId, isReconnect, token, listener)
    }

    /**
     * Start service
     */
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

        val intent = Intent(context, KmeRoomService::class.java)

        this.listener = object : IKmeWSConnectionListener {
            override fun onOpen() {

                messageManager.listen(
                    currentParticipantHandler,
                    KmeMessageEvent.ROOM_STATE
                )

                settingsModule.subscribe()

                listener.onOpen()
            }

            override fun onFailure(throwable: Throwable) {
                listener.onFailure(throwable)
            }

            override fun onClosing(code: Int, reason: String) {
                listener.onClosing(code, reason)
            }

            override fun onClosed(code: Int, reason: String) {
                stopService()
                listener.onClosed(code, reason)
            }
        }

        context.startService(intent)
        context.bindService(intent, serviceConnection, BIND_ADJUST_WITH_ACTIVITY)
    }

    /**
     * Stop service
     */
    private fun stopService() {
        val intent = Intent(context, KmeRoomService::class.java)
        context.unbindService(serviceConnection)
        context.stopService(intent)
        roomService = null
    }

    /**
     * Handle room state to store actual user data
     */
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

    /**
     * Check is socket connected
     */
    override fun isConnected(): Boolean = roomService?.isConnected() ?: false

    /**
     * Send message via socket
     */
    override fun send(message: KmeMessage<out KmeMessage.Payload>) {
        roomService?.send(message)
    }

    /**
     * Disconnect socket connection
     */
    override fun disconnect() {
        roomService?.disconnect()
    }

    /**
     * Add listeners for socket messages
     */
    override fun addListener(listener: IKmeMessageListener) {
        messageManager.addListener(listener)
    }

    /**
     * Add event to listener
     */
    override fun addListener(event: KmeMessageEvent, listener: IKmeMessageListener) {
        messageManager.addListener(event, listener)
    }

    /**
     * Start listen events for listener
     */
    override fun listen(listener: IKmeMessageListener, vararg events: KmeMessageEvent) {
        messageManager.listen(listener, *events)
    }

    /**
     * Stop listen events for listener
     */
    override fun remove(listener: IKmeMessageListener, vararg events: KmeMessageEvent) {
        messageManager.remove(listener, *events)
    }

    /**
     * Remove listener
     */
    override fun removeListener(listener: IKmeMessageListener) {
        messageManager.removeListener(listener)
    }

    /**
     * Remove all attached listeners
     */
    override fun removeListeners() {
        messageManager.removeListeners()
    }

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
    ) : IKmePeerConnectionModule? {
        return roomService?.addPublisherPeerConnection(requestedUserIdStream, renderer, listener)
    }

    /**
     * Creates a viewer connection
     */
    override fun addViewerPeerConnection(
        requestedUserIdStream: String,
        renderer: KmeSurfaceRendererView,
        listener: IKmePeerConnectionClientEvents
    ) : IKmePeerConnectionModule? {
        return roomService?.addViewerPeerConnection(requestedUserIdStream, renderer, listener)
    }

    /**
     * Getting publisher connection if exist
     */
    override fun getPublisherConnection(): IKmePeerConnectionModule? {
        return roomService?.getPublisherConnection()
    }

    /**
     * Getting publisher/viewer connection by id
     */
    override fun getPeerConnection(requestedUserIdStream: String): IKmePeerConnectionModule? {
        return roomService?.getPeerConnection(requestedUserIdStream)
    }

    /**
     * Disconnect publisher/viewer connection by id
     */
    override fun disconnectPeerConnection(requestedUserIdStream: String) {
        roomService?.disconnectPeerConnection(requestedUserIdStream)
    }

    /**
     * Disconnect all publisher/viewers connections
     */
    override fun disconnectAllConnections() {
        roomService?.disconnectAllConnections()
    }

}
