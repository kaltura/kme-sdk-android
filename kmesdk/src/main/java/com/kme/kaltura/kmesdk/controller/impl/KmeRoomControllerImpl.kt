package com.kme.kaltura.kmesdk.controller.impl

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_ADJUST_WITH_ACTIVITY
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.kme.kaltura.kmesdk.controller.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.di.KmeKoinScope
import com.kme.kaltura.kmesdk.di.getScope
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.module.*
import com.kme.kaltura.kmesdk.module.internal.IKmeInternalDataModule
import com.kme.kaltura.kmesdk.module.internal.IKmeInternalParticipantModule
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeWebRTCServer
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeRoomApiService
import com.kme.kaltura.kmesdk.service.KmeRoomService
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.messages.buildJoinBorMessage
import com.kme.kaltura.kmesdk.util.messages.buildJoinRoomMessage
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.IKmeWSConnectionListener
import com.kme.kaltura.kmesdk.ws.KmeMessageManager
import com.kme.kaltura.kmesdk.ws.KmeMessagePriority
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
    private val context: Context
) : KmeController(), IKmeRoomController {

    private val roomApiService: KmeRoomApiService by inject()
    private val userController: IKmeUserController by inject()
    private val messageManager: KmeMessageManager by inject()

    private val borSocketModule: IKmeWebSocketModule
        get() {
            val module: IKmeWebSocketModule by scopedInject(KmeKoinScope.BOR_MODULES)
            return module
        }

    private val internalParticipantModule: IKmeInternalParticipantModule by scopedInject()

    private val mainRoomSocketModule: IKmeWebSocketModule by scopedInject()
    private val settingsModule: IKmeSettingsModule by scopedInject()
    private val contentModule: IKmeContentModule by scopedInject()
    private val internalDataModule: IKmeInternalDataModule by inject()

    override val roomModule: IKmeRoomModule by scopedInject()
    override val peerConnectionModule: IKmePeerConnectionModule by scopedInject()
    override val participantModule = internalParticipantModule as IKmeParticipantModule
    override val chatModule: IKmeChatModule by scopedInject()
    override val noteModule: IKmeNoteModule by scopedInject()
    override val recordingModule: IKmeRecordingModule by scopedInject()
    override val audioModule: IKmeAudioModule by scopedInject()
    override val termsModule: IKmeTermsModule by scopedInject()
    override val breakoutModule: IKmeBreakoutModule by scopedInject()

    private val uiScope = CoroutineScope(Dispatchers.Main)

    override var webRTCServer: KmeWebRTCServer? = null
        private set

    override var roomMetadata: KmeRoomMetaData? = null
        private set

    private var roomService: KmeRoomService? = null

    private val publisherId by lazy { userController.getCurrentUserInfo()?.getUserId() }

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
            getActiveSocket().connect(
                url,
                internalDataModule.companyId,
                internalDataModule.mainRoomId,
                isReconnect,
                token,
                roomWSListener
            )
        }

        override fun onServiceDisconnected(name: ComponentName) {
            roomService = null
        }
    }

    /**
     * Connect to the room via web socket. Update actual user information first.
     */
    override fun connect(
        roomId: Long,
        roomAlias: String,
        companyId: Long,
        isReconnect: Boolean,
        roomStateListener: IKmeRoomModule.IKmeRoomStateListener
    ) {
        Log.e("TAG", "connect: $roomId")
        internalDataModule.companyId = companyId
        internalDataModule.mainRoomId = roomId
        internalDataModule.mainRoomAlias = roomAlias

        roomModule.setRoomStateListener(roomStateListener)

        userController.getUserInformation(
            roomAlias,
            success = {
                fetchWebRTCLiveServer(
                    roomAlias,
                    isReconnect,
                    roomStateListener
                )
            }, error = {
                roomStateListener.onRoomUnavailable()
//                listener.onFailure(Throwable(it))
            }
        )
    }

    private fun fetchWebRTCLiveServer(
        roomAlias: String,
        isReconnect: Boolean,
        roomStateListener: IKmeRoomModule.IKmeRoomStateListener,
    ) {
        uiScope.launch {
            safeApiCall(
                { roomApiService.getWebRTCLiveServer(roomAlias) },
                success = {
                    webRTCServer = it.data
                    if (webRTCServer?.roomInfo?.settingsV2?.general?.appAccess == KmeAppAccessValue.OFF) {
                        val appAccessException = KmeApiException.AppAccessException()
                        Log.e("TAG", "fetchWebRTCLiveServer: appAccessException", )

                        roomStateListener.onRoomUnavailable()
//                        listener.onFailure(Throwable(appAccessException))
                    } else {
                        Log.e("TAG", "fetchWebRTCLiveServer: startService", )

                        val wssUrl = it.data?.wssUrl
                        val token = it.data?.token
                        if (wssUrl != null && token != null) {
                            startService(wssUrl, isReconnect, token, roomStateListener)
                        }
                    }
                },
                error = {
                    webRTCServer = null
                    Log.e("TAG", "fetchWebRTCLiveServer: webRTCServer = null", )
                    roomStateListener.onRoomUnavailable()
//                    listener.onFailure(Throwable(it))
                }
            )
        }
    }

    /**
     * Start service
     */
    private fun startService(
        url: String,
        isReconnect: Boolean,
        token: String,
        roomStateListener: IKmeRoomModule.IKmeRoomStateListener,
    ) {
        this.url = url
        this.isReconnect = isReconnect
        this.token = token

        if (mainRoomSocketModule.isConnected()) {
            messageManager.listen(
                roomStateHandler,
                KmeMessageEvent.ROOM_STATE,
                priority = KmeMessagePriority.NORMAL
            )

            //TODO ???
            peerConnectionModule.disconnectAll()

            subscribeInternalModules()
            send(buildJoinRoomMessage(
                internalDataModule.mainRoomId,
                internalDataModule.companyId
            ))
            return
        }

        val intent = Intent(context, KmeRoomService::class.java)

        roomWSListener = object : IKmeWSConnectionListener {
            override fun onOpen() {
                subscribeInternalModules()
                send(buildJoinRoomMessage(
                    internalDataModule.mainRoomId,
                    internalDataModule.companyId
                ))
            }

            override fun onFailure(throwable: Throwable) {
                roomStateListener.onRoomUnavailable()
//                listener.onFailure(throwable)
            }

            override fun onClosing(code: Int, reason: String) {
                stopService()
                roomStateListener.onRoomUnavailable()
//                listener.onClosing(code, reason)
            }

            override fun onClosed(code: Int, reason: String) {
                stopService()
                roomStateListener.onRoomUnavailable()
//                listener.onClosed(code, reason)
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
        try {
            context.unbindService(serviceConnection)
        } catch (ex: IllegalArgumentException) {
            //Service is not bound
        }
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
        roomStateListener: IKmeRoomModule.IKmeRoomStateListener
    ) {
        roomModule.setRoomStateListener(roomStateListener)

        uiScope.launch {
            safeApiCall(
                { roomApiService.getWebRTCLiveServer(roomAlias) },
                success = {
                    val wssUrl = it.data?.wssUrl
                    val token = it.data?.token
                    if (wssUrl != null && token != null) {
//                        participantModule.changeMediaState(
//                            roomId,
//                            internalDataModule.companyId,
//                            publisherId!!,
//                            KmeMediaStateType.LIVE_MEDIA,
//                            KmeMediaDeviceState.DISABLED
//                        )

//                        internalDataModule.breakoutRoomId = roomId

                        getActiveSocket().connect(
                            wssUrl,
                            internalDataModule.companyId,
                            roomId,
                            isReconnect,
                            token,
                            object : IKmeWSConnectionListener {
                                override fun onOpen() {
//                                    messageManager.removeListeners()
                                    subscribeInternalModules()

                                    send(buildJoinRoomMessage(
                                        internalDataModule.breakoutRoomId,
                                        internalDataModule.companyId
                                    ))

                                    peerConnectionModule.disconnectAll()

                                    mainRoomSocketModule.send(
                                        buildJoinBorMessage(
                                            internalDataModule.mainRoomId,
                                            internalDataModule.companyId,
                                            userController.getCurrentUserInfo()?.getUserId(),
                                            internalDataModule.breakoutRoomId
                                        )
                                    )
                                }

                                override fun onFailure(throwable: Throwable) {
                                    roomStateListener.onRoomUnavailable()
//                                    listener.onFailure(throwable)
                                }

                                override fun onClosing(code: Int, reason: String) {
                                    roomStateListener.onRoomUnavailable()
//                                    listener.onClosing(code, reason)
                                }

                                override fun onClosed(code: Int, reason: String) {
                                    roomStateListener.onRoomUnavailable()
//                                    listener.onClosed(code, reason)
                                    releaseScope(getScope(KmeKoinScope.BOR_MODULES))
                                }
                            }
                        )
                    }
                },
                error = {
                    roomStateListener.onRoomUnavailable()
//                    listener.onFailure(Throwable(it))
                }
            )
        }

    }

    override fun subscribeForContent(listener: IKmeContentModule.KmeContentListener) {
        contentModule.subscribe(listener)
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
        Log.e("KmeRoomController", "disconnect main: ${mainRoomSocketModule.hashCode()}")
        Log.e("KmeRoomController", "disconnect bor: ${borSocketModule.hashCode()}")

        if (mainRoomSocketModule.isConnected())
            mainRoomSocketModule.disconnect()

        if (borSocketModule.isConnected())
            borSocketModule.disconnect()

        peerConnectionModule.disconnectAll()
        audioModule.stop()

        internalDataModule.clear()
    }

    /**
     * Add listeners for socket messages
     */
    override fun addListener(
        listener: IKmeMessageListener,
        priority: KmeMessagePriority
    ) {
        messageManager.addListener(listener, priority)
    }

    /**
     * Add event to listener
     */
    override fun addListener(
        event: KmeMessageEvent,
        listener: IKmeMessageListener,
        priority: KmeMessagePriority
    ) {
        messageManager.addListener(event, listener, priority)
    }

    /**
     * Start listen events for listener
     */
    override fun listen(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent,
        priority: KmeMessagePriority
    ): IKmeMessageListener {
        return messageManager.listen(listener, *events, priority = priority)
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

    private fun getActiveSocket() = if (internalDataModule.breakoutRoomId != 0L) {
        borSocketModule
    } else {
        mainRoomSocketModule
    }

    private fun subscribeInternalModules() {
        roomModule.subscribe()
        internalParticipantModule.subscribe()
        settingsModule.subscribe()
        breakoutModule.subscribe()
    }

}
