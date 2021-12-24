package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeContentModule
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomModule
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomModule.IKmeRoomStateListener
import com.kme.kaltura.kmesdk.controller.room.internal.IKmeInternalDataModule
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomInfoResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomsResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeJoinRoomResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeRoomApiService
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.messages.*
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeRoomExitReason
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage.ParticipantRemovedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage.ApprovalPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage.CloseWebSocketPayload
import com.kme.kaltura.kmesdk.ws.message.room.KmeRoomMetaData
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * An implementation for room actions
 */
class KmeRoomModuleImpl : KmeController(), IKmeRoomModule {

    private val roomController: IKmeRoomController by scopedInject()
    private val internalDataModule: IKmeInternalDataModule by inject()
    private val roomApiService: KmeRoomApiService by inject()
    private val userController: IKmeUserController by inject()
    private val contentModule: IKmeContentModule by scopedInject()

    private val uiScope = CoroutineScope(Dispatchers.Main)

    private val publisherId by lazy { userController.getCurrentUserInfo()?.getUserId() ?: 0 }
    private var stateListener: IKmeRoomStateListener? = null
    private var roomData: KmeRoomMetaData? = null

    /**
     * Subscribing for the room events
     */
    override fun subscribe() {
        roomController.listen(
            roomStateHandler,
            KmeMessageEvent.ROOM_STATE,
            KmeMessageEvent.MODULE_STATE
        )

        roomController.listen(
            roomExitHandler,
            KmeMessageEvent.USER_REJECTED_BY_INSTRUCTOR,
            KmeMessageEvent.USER_REMOVED,
            KmeMessageEvent.CLOSE_WEB_SOCKET
        )
    }

    /**
     * Setting listener for basic room states
     */
    override fun setRoomStateListener(stateListener: IKmeRoomStateListener) {
        this.stateListener = stateListener
    }

    /**
     * Listen for room state event
     */
    private val roomStateHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.ROOM_STATE -> {
                    val stateMessage: KmeRoomInitModuleMessage<KmeRoomInitModuleMessage.RoomStatePayload>? =
                        message.toType()
                    stateMessage?.let { msg ->
                        roomData = msg.payload?.metaData

                        val participantsList = msg.payload?.participants?.values?.toMutableList()
                        val currentParticipant = participantsList?.find { participant ->
                            participant.userId == publisherId
                        }
                        currentParticipant?.userPermissions =
                            roomController.webRTCServer?.roomInfo?.settingsV2

                        userController.updateParticipant(currentParticipant)

                        val roomId = if (internalDataModule.breakoutRoomId != 0L) {
                            internalDataModule.breakoutRoomId
                        } else {
                            internalDataModule.mainRoomId
                        }

                        roomController.send(
                            buildGetQuickPollStateMessage(
                                roomId,
                                internalDataModule.companyId
                            )
                        )

                        // TODO: TC ?

                        if (internalDataModule.mainRoomId == roomId) {
                            roomController.send(
                                buildGetBreakoutStateMessage(
                                    roomId,
                                    internalDataModule.companyId
                                )
                            )
                        }
                    }
                }
                KmeMessageEvent.MODULE_STATE -> {
                    val msg: KmeBreakoutModuleMessage<KmeBreakoutModuleMessage.BreakoutRoomState>? =
                        message.toType()
                    msg?.let {
                        // TODO: implement prioritized set
                        roomData?.let { stateListener?.onRoomAvailable(it) }
                    }
                }
                else -> {
                }
            }
        }
    }

    /**
     * Listen for exit events
     */
    private val roomExitHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.USER_REJECTED_BY_INSTRUCTOR -> {
                    val msg: KmeRoomInitModuleMessage<ApprovalPayload>? = message.toType()
                    msg?.payload?.userId?.let { userId ->
                        if (userId == publisherId) {
                            stateListener?.onRoomExit(KmeRoomExitReason.REMOVED_USER)
                        }
                    }
                }
                KmeMessageEvent.USER_REMOVED -> {
                    val msg: KmeParticipantsModuleMessage<ParticipantRemovedPayload>? =
                        message.toType()
                    msg?.payload?.targetUserId?.let { userId ->
                        if (userId == publisherId) {
                            stateListener?.onRoomExit(KmeRoomExitReason.REMOVED_USER)
                        }
                    }
                }
                KmeMessageEvent.CLOSE_WEB_SOCKET -> {
                    val msg: KmeRoomInitModuleMessage<CloseWebSocketPayload>? = message.toType()
                    msg?.payload?.reason?.let { reason ->
                        stateListener?.onRoomExit(reason)
                    }
                }
                else -> {
                }
            }
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
     * Handling cookies for login via deep linking
     */
    override fun join(
        hash: String,
        success: (response: KmeJoinRoomResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomApiService.join(hash, 1) },
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
                { roomApiService.getRoomInfo(alias, withFiles, checkPermission) },
                success,
                error
            )
        }
    }

    /**
     * Joining the room
     */
    override fun joinRoom(roomId: Long, companyId: Long, password: String) {
        roomController.send(buildRoomPasswordMessage(roomId, companyId, password))
    }

    override fun getMainRoomId(): Long {
        return internalDataModule.mainRoomId
    }

    override fun getMainRoomAlias(): String {
        return internalDataModule.mainRoomAlias
    }

    /**
     * Changing setting value for room
     */
    override fun changeRoomSettings(
        roomId: Long,
        userId: Long,
        key: KmePermissionKey,
        value: KmePermissionValue
    ) {
        roomController.send(buildRoomSettingsChangedMessage(roomId, userId, key, value))
    }

    /**
     * Change current room content view
     */
    override fun setActiveContent(view: KmeContentType) {
        // Server side issue
        val userId = if (view == KmeContentType.CONFERENCE_VIEW) {
            null
        } else {
            publisherId
        }
        roomController.send(buildSetActiveContentMessage(userId, view))
    }

    /**
     * Subscribes to the shared content in the room
     */
    override fun subscribeForContent(listener: IKmeContentModule.KmeContentListener) {
        contentModule.subscribe(listener)
    }

    override fun muteActiveContent(isMute: Boolean) {
        contentModule.muteActiveContent(isMute)
    }

    /**
     * Ends active room session
     */
    override fun endSession(roomId: Long, companyId: Long) {
        roomController.send(buildEndSessionMessage(roomId, companyId))
        roomController.removeListeners()
    }

    /**
     * Ends active room session
     */
    override fun endSessionForEveryone(roomId: Long, companyId: Long) {
        roomController.send(buildEndSessionForEveryoneMessage(roomId, companyId))
        roomController.removeListeners()
    }

}
