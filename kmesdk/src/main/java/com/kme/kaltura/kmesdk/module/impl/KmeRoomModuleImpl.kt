package com.kme.kaltura.kmesdk.module.impl

import android.util.Log
import com.kme.kaltura.kmesdk.controller.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.module.IKmeContentModule
import com.kme.kaltura.kmesdk.module.IKmeRoomModule
import com.kme.kaltura.kmesdk.module.IKmeRoomModule.IKmeRoomStateListener
import com.kme.kaltura.kmesdk.module.IKmeTermsModule
import com.kme.kaltura.kmesdk.module.internal.IKmeInternalDataModule
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.request.xlroom.XlRoomLaunchRequest
import com.kme.kaltura.kmesdk.rest.request.xlroom.XlRoomPrepareRequest
import com.kme.kaltura.kmesdk.rest.request.xlroom.XlRoomStopRequest
import com.kme.kaltura.kmesdk.rest.response.room.*
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeRoomApiService
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.messages.*
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.KmeMessagePriority
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
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
    private val termsModule: IKmeTermsModule by scopedInject()

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
            priority = KmeMessagePriority.NORMAL
        )

        roomController.listen(
            breakoutRoomStateHandler,
            KmeMessageEvent.MODULE_STATE,
            priority = KmeMessagePriority.LOW
        )

        roomController.listen(
            bannersHandler,
            KmeMessageEvent.ANY_INSTRUCTORS_IS_CONNECTED_TO_ROOM,
            KmeMessageEvent.ROOM_HAS_PASSWORD,
            KmeMessageEvent.ROOM_PARTICIPANT_LIMIT_REACHED,
            KmeMessageEvent.AWAIT_INSTRUCTOR_APPROVAL,
            KmeMessageEvent.USER_APPROVED_BY_INSTRUCTOR,
            KmeMessageEvent.USER_REJECTED_BY_INSTRUCTOR,
            KmeMessageEvent.JOINED_ROOM,
            KmeMessageEvent.ROOM_PASSWORD_STATUS_RECEIVED,
            KmeMessageEvent.INSTRUCTOR_IS_OFFLINE,
            priority = KmeMessagePriority.HIGH
        )

        roomController.listen(
            termsHandler,
            KmeMessageEvent.TERMS_NEEDED,
            KmeMessageEvent.TERMS_AGREED,
            KmeMessageEvent.TERMS_REJECTED,
            KmeMessageEvent.SET_TERMS_AGREEMENT,
            priority = KmeMessagePriority.HIGH
        )

        roomController.listen(
            roomExitHandler,
            KmeMessageEvent.USER_REJECTED_BY_INSTRUCTOR,
            KmeMessageEvent.USER_REMOVED,
            KmeMessageEvent.CLOSE_WEB_SOCKET,
            priority = KmeMessagePriority.HIGH
        )
    }

    /**
     * Setting listener for basic room states
     */
    override fun setRoomStateListener(stateListener: IKmeRoomStateListener?) {
        this.stateListener = stateListener
    }

    /**
     * Getting listener for basic room states
     */
    override fun getRoomStateListener(): IKmeRoomStateListener? = this.stateListener

    /**
     * Getting current room id
     */
    override fun getCurrentRoomId() = roomController.breakoutModule.getAssignedBreakoutRoom()?.id
        ?: roomController.roomModule.getMainRoomId()

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
                        } else {
                            Log.e("TAG", "roomStateHandler: onRoomAvailable")
                            roomData?.let { stateListener?.onRoomAvailable(it) }
                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    private val breakoutRoomStateHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            if (KmeMessageModule.BREAKOUT == message.module) {
                when (message.name) {
                    KmeMessageEvent.MODULE_STATE -> {
                        val msg: KmeBreakoutModuleMessage<KmeBreakoutModuleMessage.BreakoutRoomState>? =
                            message.toType()
                        msg?.let {
                            roomData?.let { stateListener?.onRoomAvailable(it) }
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private val bannersHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.AWAIT_INSTRUCTOR_APPROVAL,
                KmeMessageEvent.USER_APPROVED_BY_INSTRUCTOR,
                KmeMessageEvent.USER_REJECTED_BY_INSTRUCTOR,
                KmeMessageEvent.ANY_INSTRUCTORS_IS_CONNECTED_TO_ROOM,
                KmeMessageEvent.ROOM_HAS_PASSWORD,
                KmeMessageEvent.ROOM_PASSWORD_STATUS_RECEIVED,
                KmeMessageEvent.INSTRUCTOR_IS_OFFLINE,
                KmeMessageEvent.ROOM_PARTICIPANT_LIMIT_REACHED -> {
                    stateListener?.onRoomBanner(message)
                }
                else -> {
                }
            }
        }
    }

    private val termsHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.TERMS_NEEDED -> {
                    termsModule.getTermsMessage(roomId = internalDataModule.mainRoomId,
                        companyId = internalDataModule.companyId,
                        success = {
                            stateListener?.onRoomTerms(IKmeTermsModule.KmeTermsType.NEEDED, it.data?.terms)
                        },
                        error = {
                            stateListener?.onRoomTerms(IKmeTermsModule.KmeTermsType.NEEDED)
                        })
                }
                KmeMessageEvent.TERMS_AGREED -> {
                    stateListener?.onRoomTerms(IKmeTermsModule.KmeTermsType.ACCEPTED)
                }
                KmeMessageEvent.TERMS_REJECTED -> {
                    stateListener?.onRoomTerms(IKmeTermsModule.KmeTermsType.REJECTED)
                }
                KmeMessageEvent.SET_TERMS_AGREEMENT -> {
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
        getXlRoomState(roomId, companyId)
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
        if (!userController.isModerator())
            return
        roomController.send(buildRoomSettingsChangedMessage(roomId, userId, key, value))
    }

    /**
     * Change current room content view
     */
    override fun setActiveContent(view: KmeContentType) {
        if (!userController.isModerator())
            return
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
        if (!userController.isModerator())
            return
        roomController.send(buildEndSessionForEveryoneMessage(roomId, companyId))
        roomController.removeListeners()
    }

    override fun getXlRoomState(roomId: Long, companyId: Long) {
        if (userController.isModerator()) {
            roomController.send(buildXlRoomGetStateMessage(roomId, companyId))
        }
    }

    /**
     * Start initiating xl room
     */
    override fun prepareXlRoom(
        roomId: Long,
        userId: Long,
        participants: Int,
        presenters: Int,
        regionId: String,
        global: Boolean,
        success: (response: KmeXlRoomPrepareResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        if (!userController.isModerator())
            return

        uiScope.launch {
            safeApiCall(
                {
                    roomApiService.prepareXlRoom(
                        XlRoomPrepareRequest(
                            roomId,
                            userId,
                            participants,
                            presenters,
                            regionId,
                            global
                        )
                    )
                },
                success,
                error
            )
        }
    }

    /**
     * Launch xl room
     */
    override fun launchXlRoom(
        roomId: Long,
        success: (response: KmeXlRoomLaunchResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        if (!userController.isModerator())
            return

        uiScope.launch {
            safeApiCall(
                { roomApiService.launchXlRoom(XlRoomLaunchRequest(roomId)) },
                success,
                error
            )
        }
    }

    /**
     * Stop xl room initiation
     */
    override fun stopXlRoom(
        roomId: Long,
        companyId: Long,
        success: (response: KmeXlRoomStopResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        if (!userController.isModerator())
            return

        uiScope.launch {
            safeApiCall(
                { roomApiService.stopXlRoom(XlRoomStopRequest(roomId, companyId)) },
                success,
                error
            )
        }
    }


}
