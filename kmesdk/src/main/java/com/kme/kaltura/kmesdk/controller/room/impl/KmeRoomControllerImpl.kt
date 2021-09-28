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
import com.kme.kaltura.kmesdk.di.KmeKoinScope
import com.kme.kaltura.kmesdk.di.getScope
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeWebRTCServer
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeRoomApiService
import com.kme.kaltura.kmesdk.service.KmeRoomService
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.IKmeWSConnectionListener
import com.kme.kaltura.kmesdk.ws.KmeMessageManager
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage.RoomStatePayload
import com.kme.kaltura.kmesdk.ws.message.room.KmeRoomMetaData
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmeAppAccessValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * An implementation for room data
 */
class KmeRoomControllerImpl(
    private val context: Context,
) : KmeController(), IKmeRoomController {

    private val roomApiService: KmeRoomApiService by inject()
    private val userController: IKmeUserController by inject()
    private val messageManager: KmeMessageManager by inject()

    private val borSocketModule: IKmeWebSocketModule
        get() {
            val module: IKmeWebSocketModule by scopedInject(KmeKoinScope.BOR_MODULES)
            return module
        }
    private val mainRoomSocketModule: IKmeWebSocketModule by scopedInject()
    private val settingsModule: IKmeSettingsModule by scopedInject()
    private val contentModule: IKmeContentModule by scopedInject()

    override val roomModule: IKmeRoomModule by scopedInject()
    override val peerConnectionModule: IKmePeerConnectionModule by scopedInject()
    override val participantModule: IKmeParticipantModule by scopedInject()
    override val chatModule: IKmeChatModule by scopedInject()
    override val noteModule: IKmeNoteModule by scopedInject()
    override val recordingModule: IKmeRecordingModule by scopedInject()
    override val audioModule: IKmeAudioModule by scopedInject()
    override val breakoutModule: IKmeBreakoutModule by scopedInject()

    private val uiScope = CoroutineScope(Dispatchers.Main)

    override var roomSettings: KmeWebRTCServer? = null
        private set

    override var roomMetadata: KmeRoomMetaData? = null
        private set

    private var roomService: KmeRoomService? = null

    private var companyId: Long = 0
    private var roomId: Long = 0
    private var breakoutRoomId: Long? = null
    private var isReconnect: Boolean = true

    private lateinit var url: String
    private lateinit var token: String
    private lateinit var roomWSListener: IKmeWSConnectionListener

    /**
     * ServiceConnection block to pass data related to the room
     */
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder: KmeRoomService.RoomServiceBinder =
                service as KmeRoomService.RoomServiceBinder
            roomService = binder.service
            getActiveSocket().connect(url, companyId, roomId, isReconnect, token, roomWSListener)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            roomService = null
        }
    }

    // TODO: avoid passing roomId and companyId to the KME controllers
    /**
     * Getting actual room id
     */
    override fun getRoomId() = roomId

    /**
     * Getting actual company id
     */
    override fun getCompanyId() = companyId

    /**
     * Connect to the room via web socket. Update actual user information first.
     */
    override fun connect(
        roomId: Long,
        roomAlias: String,
        companyId: Long,
        isReconnect: Boolean,
        exitListener: IKmeRoomModule.ExitRoomListener,
        listener: IKmeWSConnectionListener,
    ) {
        roomModule.setExitListener(exitListener)

        userController.getUserInformation(
            roomAlias,
            success = {
                fetchWebRTCLiveServer(
                    roomId,
                    roomAlias,
                    companyId,
                    isReconnect,
                    listener
                )
            }, error = {
                listener.onFailure(Throwable(it))
            }
        )
    }

    private fun fetchWebRTCLiveServer(
        roomId: Long,
        roomAlias: String,
        companyId: Long,
        isReconnect: Boolean,
        listener: IKmeWSConnectionListener,
    ) {
        uiScope.launch {
            safeApiCall(
                { roomApiService.getWebRTCLiveServer(roomAlias) },
                success = {
                    roomSettings = it.data
                    if (roomSettings?.roomInfo?.settingsV2?.general?.appAccess == KmeAppAccessValue.OFF) {
                        val appAccessException = KmeApiException.AppAccessException()
                        listener.onFailure(Throwable(appAccessException))
                    } else {
                        val wssUrl = it.data?.wssUrl
                        val token = it.data?.token
                        if (wssUrl != null && token != null) {
                            startService(wssUrl, companyId, roomId, isReconnect, token, listener)
                        }
                    }
                },
                error = {
                    roomSettings = null
                    listener.onFailure(Throwable(it))
                }
            )
        }
    }

    /**
     * Subscribes to the shared content in the room
     */
    override fun subscribeForContent(listener: IKmeContentModule.KmeContentListener) {
        contentModule.subscribe(listener)
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
        listener: IKmeWSConnectionListener,
    ) {
        this.url = url
        this.companyId = companyId
        this.roomId = roomId
        this.isReconnect = isReconnect
        this.token = token

        if (mainRoomSocketModule.isConnected()) {
            messageManager.listen(
                roomStateHandler,
                KmeMessageEvent.ROOM_STATE,
                KmeMessageEvent.GET_MODULE_STATE
            )

            peerConnectionModule.disconnectAll()

            subscribeInternalModules()

            listener.onOpen()
            return
        }

        val intent = Intent(context, KmeRoomService::class.java)

        roomWSListener = object : IKmeWSConnectionListener {
            override fun onOpen() {
                messageManager.listen(
                    roomStateHandler,
                    KmeMessageEvent.ROOM_STATE,
                    KmeMessageEvent.GET_MODULE_STATE
                )

                subscribeInternalModules()

                listener.onOpen()
            }

            override fun onFailure(throwable: Throwable) {
                listener.onFailure(throwable)
            }

            override fun onClosing(code: Int, reason: String) {
                stopService()
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
    private val roomStateHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.ROOM_STATE -> {
                    val msg: KmeRoomInitModuleMessage<RoomStatePayload>? = message.toType()

                    roomMetadata = msg?.payload?.metaData

                    val participantsList = msg?.payload?.participants?.values?.toMutableList()
                    val currentParticipant = participantsList?.find { participant ->
                        participant.userId == userController.getCurrentUserInfo()?.getUserId()
                    }
                    currentParticipant?.userPermissions = roomSettings?.roomInfo?.settingsV2

                    userController.updateParticipant(currentParticipant)
                }
                else -> {
                }
            }
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
        listener: IKmeWSConnectionListener,
    ) { /*Nothing to do*/
    }

    override fun connectToBreakout(
        roomId: Long,
        roomAlias: String,
        listener: IKmeWSConnectionListener,
    ) {
        uiScope.launch {
            safeApiCall(
                { roomApiService.getWebRTCLiveServer(roomAlias) },
                success = {
                    val wssUrl = it.data?.wssUrl
                    val token = it.data?.token
                    if (wssUrl != null && token != null) {
                        breakoutRoomId = roomId
                        getActiveSocket().connect(
                            wssUrl,
                            companyId,
                            roomId,
                            isReconnect,
                            token,
                            object : IKmeWSConnectionListener {
                                override fun onOpen() {
                                    messageManager.removeListeners()
                                    subscribeInternalModules()

                                    peerConnectionModule.disconnectAll()

                                    listener.onOpen()
                                }

                                override fun onFailure(throwable: Throwable) {
                                    listener.onFailure(throwable)
                                }

                                override fun onClosing(code: Int, reason: String) {
                                    listener.onClosing(code, reason)
                                    releaseScope(getScope(KmeKoinScope.BOR_MODULES))
                                    breakoutRoomId = null
                                }

                                override fun onClosed(code: Int, reason: String) {
                                    listener.onClosed(code, reason)
                                    releaseScope(getScope(KmeKoinScope.BOR_MODULES))
                                    breakoutRoomId = null
                                }
                            }
                        )
                    }
                },
                error = {
                    listener.onFailure(Throwable(it))
                }
            )
        }

    }

    /**
     * Check is socket connected
     */
    override fun isConnected() = getActiveSocket().isConnected()

    /**
     * Send message via socket
     */
    override fun send(message: KmeMessage<out KmeMessage.Payload>) {
        getActiveSocket().send(message)
    }

    /**
     * Disconnect from the room
     */
    override fun disconnect() {
        if (mainRoomSocketModule.isConnected())
            mainRoomSocketModule.disconnect()

        peerConnectionModule.disconnectAll()
        audioModule.stop()
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
    override fun addListener(
        event: KmeMessageEvent,
        listener: IKmeMessageListener,
    ) {
        messageManager.addListener(event, listener)
    }

    /**
     * Start listen events for listener
     */
    override fun listen(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent,
    ): IKmeMessageListener {
        return messageManager.listen(listener, *events)
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

    private fun getActiveSocket(): IKmeWebSocketModule = breakoutRoomId?.let {
        borSocketModule
    } ?: run {
        mainRoomSocketModule
    }

    private fun subscribeInternalModules() {
        roomModule.subscribe()
        settingsModule.subscribe()
        breakoutModule.subscribe()
    }

}
