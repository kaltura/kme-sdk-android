package com.kme.kaltura.kmesdk.module.impl

import com.kme.kaltura.kmesdk.controller.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.di.KmeKoinScope
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.ifNonNull
import com.kme.kaltura.kmesdk.module.IKmeBreakoutModule
import com.kme.kaltura.kmesdk.module.IKmeBreakoutModule.IKmeBreakoutEvents
import com.kme.kaltura.kmesdk.module.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.module.internal.IKmeInternalDataModule
import com.kme.kaltura.kmesdk.module.internal.IKmeInternalParticipantModule
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.messages.buildAssignUserBorMessage
import com.kme.kaltura.kmesdk.util.messages.buildCallToInstructorMessage
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.KmeMessagePriority
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeBreakoutRoomStatusType
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaStateType
import org.koin.core.inject

/**
 * An implementation for actions with breakout rooms
 */
class KmeBreakoutModuleImpl : KmeController(), IKmeBreakoutModule {

    private val roomController: IKmeRoomController by scopedInject()
    private val borSocketModule: IKmeWebSocketModule
        get() {
            val module: IKmeWebSocketModule by scopedInject(KmeKoinScope.BOR_MODULES)
            return module
        }
    private val mainRoomSocketModule: IKmeWebSocketModule by scopedInject()
    private val internalDataModule: IKmeInternalDataModule by inject()
    private val participantModule: IKmeInternalParticipantModule by scopedInject()
    private val userController: IKmeUserController by inject()

    private val currentUserId by lazy { userController.getCurrentUserInfo()?.getUserId() ?: 0 }

    private var borState: BreakoutRoomState? = null
    private var eventListener: IKmeBreakoutEvents? = null
    private var selfAssignedBorId: Long? = null

    /**
     * Subscribing for the room events related breakout rooms
     */
    override fun subscribe() {
        roomController.listen(
            breakoutRoomMessageHandler,
            KmeMessageEvent.MODULE_STATE,
            KmeMessageEvent.BREAKOUT_START_SUCCESS,
            KmeMessageEvent.BREAKOUT_STOP_SUCCESS,
            KmeMessageEvent.BREAKOUT_ADD_ROOM_SUCCESS,
            KmeMessageEvent.BREAKOUT_DELETE_ROOM_SUCCESS,
            KmeMessageEvent.BREAKOUT_CHANGE_ROOM_NAME_SUCCESS,
            KmeMessageEvent.BREAKOUT_ASSIGN_PARTICIPANTS_SUCCESS,
            KmeMessageEvent.BREAKOUT_MOVE_TO_NEXT_ROOM,
            KmeMessageEvent.BREAKOUT_RESHUFFLE_ASSIGNMENTS_SUCCESS,
            KmeMessageEvent.BREAKOUT_CLEAR_ASSIGNMENTS_SUCCESS,
            KmeMessageEvent.BREAKOUT_MODERATOR_JOINED_SUCCESS,
            KmeMessageEvent.BREAKOUT_USER_JOINED_SUCCESS,
            KmeMessageEvent.BREAKOUT_EXTEND_TIME_LIMIT_SUCCESS,
            KmeMessageEvent.BREAKOUT_CALL_TO_INSTRUCTOR_SUCCESS,
            KmeMessageEvent.BREAKOUT_INSTRUCTOR_MESSAGE,
            priority = KmeMessagePriority.HIGH
        )
    }

    /**
     * Handle breakout room setting changes from main room
     */
    override fun onSettingChanged(payload: KmeRoomSettingsModuleMessage.RoomDefaultSettingsChangedPayload) {

    }

    /**
     * Setting events listener
     */
    override fun setEventsListener(listener: IKmeBreakoutEvents) {
        eventListener = listener
    }

    /**
     * Assign self to specific breakout room
     */
    override fun assignSelfToBor(breakoutRoomId: Long) {
        selfAssignedBorId = breakoutRoomId
        assignUserToBor(currentUserId, breakoutRoomId)
    }

    /**
     * Assign user to specific room
     */
    override fun assignUserToBor(
        userId: Long,
        breakoutRoomId: Long
    ) {
        var validBreakoutRoomId = breakoutRoomId

        if (internalDataModule.mainRoomId == breakoutRoomId) {
            validBreakoutRoomId = 0
        }

        mainRoomSocketModule.send(
            buildAssignUserBorMessage(
                internalDataModule.mainRoomId,
                internalDataModule.companyId,
                userId,
                validBreakoutRoomId
            )
        )
    }

    /**
     * Call instructor
     */
    override fun callToInstructor() {
        borState?.breakoutRooms?.find {
            it.id == internalDataModule.breakoutRoomId &&
                    (it.raisedHandUserId == null || it.raisedHandUserId == currentUserId)
        }?.let { room ->
            mainRoomSocketModule.send(
                buildCallToInstructorMessage(
                    internalDataModule.mainRoomId,
                    internalDataModule.companyId,
                    currentUserId,
                    internalDataModule.breakoutRoomId
                )
            )
        }
    }

    /**
     * Getting list of breakout rooms
     */
    override fun getBreakoutState() = borState

    /**
     * Checking is breakout rooms currently active
     */
    override fun isActive() = borState?.status == KmeBreakoutRoomStatusType.ACTIVE

    /**
     * Getting breakout room in case that user assigned to any bor
     */
    override fun getAssignedBreakoutRoom(): BreakoutRoom? {
        if (!isActive()) return null

        val breakoutRoomId = borState?.assignments?.find { assignment ->
            assignment.userId == currentUserId
        }?.breakoutRoomId

        return borState?.breakoutRooms?.find { room -> breakoutRoomId == room.id }
    }

    /**
     * Listen for subscribed events
     */
    private val breakoutRoomMessageHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.MODULE_STATE -> {
                    if (KmeMessageModule.BREAKOUT == message.module) {
                        val msg: KmeBreakoutModuleMessage<BreakoutRoomState>? = message.toType()
                        msg?.let {
                            borState = it.payload

                            eventListener?.onBreakoutRoomStateChanged()

                            if (it.payload?.status == KmeBreakoutRoomStatusType.ACTIVE) {
                                handleJoinRoom(it.payload)
                            }
                        }
                    }
                }
                KmeMessageEvent.BREAKOUT_START_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomState>? = message.toType()
                    borState?.status = KmeBreakoutRoomStatusType.ACTIVE
                    msg?.payload?.assignments?.let {
                        borState?.assignments = it
                    }
                    msg?.payload?.breakoutRooms?.forEach { room ->
                        borState?.breakoutRooms?.find { localRoom ->
                            localRoom.id == room.id
                        }?.let {
                            it.participantsCount = room.participantsCount
                        }
                    }
                    borState?.startTime = msg?.payload?.startTime
                    borState?.endTime = msg?.payload?.endTime

                    eventListener?.onBreakoutRoomStateChanged()

                    handleJoinRoom(msg?.payload)
                }
                KmeMessageEvent.BREAKOUT_STOP_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomState>? = message.toType()
                    borState?.status = KmeBreakoutRoomStatusType.NON_ACTIVE
                    msg?.payload?.breakoutRooms?.forEach { room ->
                        room.raisedHandUserId = null
                    }
                    handleLeaveRoom()
                }
                KmeMessageEvent.BREAKOUT_ADD_ROOM_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutAddRoomPayload>? = message.toType()
                    msg?.payload?.room?.let {
                        borState?.breakoutRooms?.add(it)
                    }
                    eventListener?.onBreakoutRoomStateChanged()
                }
                KmeMessageEvent.BREAKOUT_DELETE_ROOM_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomState>? = message.toType()
                    msg?.payload?.breakoutRooms?.let {
                        borState?.breakoutRooms?.clear()
                        borState?.breakoutRooms?.addAll(it)
                    }
                    msg?.payload?.removedAssignments?.forEach { removedAssignment ->
                        borState?.assignments?.removeAll { assignment ->
                            assignment.userId == removedAssignment.userId
                        }
                    }
                    eventListener?.onBreakoutRoomStateChanged()
                }
                KmeMessageEvent.BREAKOUT_CHANGE_ROOM_NAME_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutChangeNamePayload>? = message.toType()
                    borState?.breakoutRooms?.find { room ->
                        room.id == msg?.payload?.room?.id
                    }?.let {
                        it.name = msg?.payload?.room?.name
                    }
                    eventListener?.onBreakoutRoomStateChanged()
                }
                KmeMessageEvent.BREAKOUT_ASSIGN_PARTICIPANTS_SUCCESS,
                KmeMessageEvent.BREAKOUT_MOVE_TO_NEXT_ROOM,
                KmeMessageEvent.BREAKOUT_RESHUFFLE_ASSIGNMENTS_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomState>? = message.toType()
                    handleAssignments(msg?.payload)
                }
                KmeMessageEvent.BREAKOUT_CLEAR_ASSIGNMENTS_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutPayload>? = message.toType()
                    borState?.assignments?.clear()
                    borState?.breakoutRooms?.forEach { room ->
                        room.participantsCount = 0
                    }
                    eventListener?.onBreakoutRoomStateChanged()
                }
                KmeMessageEvent.BREAKOUT_MODERATOR_JOINED_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomState>? = message.toType()
                    msg?.payload?.assignments?.get(0)?.let { assignment ->
                        borState?.assignments?.find { localAssignment ->
                            localAssignment.userId == assignment.userId
                        }?.let {
                            it.status = assignment.status
                        }
                        borState?.breakoutRooms?.find { room ->
                            room.id == assignment.breakoutRoomId
                        }?.let {
                            it.raisedHandUserId = null
                        }
                    }
                    eventListener?.onBreakoutRoomStateChanged()
                }
                KmeMessageEvent.BREAKOUT_USER_JOINED_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomState>? = message.toType()
                    msg?.payload?.assignments?.get(0)?.let { assignment ->
                        borState?.assignments?.find { localAssignment ->
                            localAssignment.userId == assignment.userId
                        }?.let {
                            it.status = assignment.status
                        }
                    }
                    eventListener?.onBreakoutRoomStateChanged()
                }
                KmeMessageEvent.BREAKOUT_EXTEND_TIME_LIMIT_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutExtendTimePayload>? = message.toType()
                    borState?.startTime = msg?.payload?.start
                    borState?.endTime = msg?.payload?.end
                    eventListener?.onBreakoutTimeExtended()
                }
                KmeMessageEvent.BREAKOUT_CALL_TO_INSTRUCTOR_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomState>? = message.toType()
                    msg?.payload?.breakoutRooms?.firstOrNull()?.id?.let { callRoomId ->
                        borState?.breakoutRooms?.find { room ->
                            room.id == callRoomId
                        }?.let { breakoutRoom ->
                            val callUserId =
                                msg.payload?.breakoutRooms?.firstOrNull()?.raisedHandUserId
                            breakoutRoom.raisedHandUserId = callUserId
                            eventListener?.onBreakoutCallInstructor(callRoomId, callUserId)
                        }
                    }
                }
                KmeMessageEvent.BREAKOUT_INSTRUCTOR_MESSAGE -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutMessagePayload>? = message.toType()
                    ifNonNull(
                        msg?.payload?.messageType,
                        msg?.payload?.messageMetadata
                    ) { messageType, messageMetadata ->
                        messageMetadata.senderAvatar =
                            roomController.participantModule.getParticipant(messageMetadata.senderId)?.avatar

                        eventListener?.onBreakoutInstructorMessage(messageType, messageMetadata)
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun handleJoinRoom(payload: BreakoutRoomState?) {
        borState?.breakoutRooms?.find { room ->
            room.id == payload?.assignments?.find { assignment ->
                assignment.userId == currentUserId
            }?.breakoutRoomId
        }?.let { breakoutRoom ->
            ifNonNull(breakoutRoom.id, breakoutRoom.alias) { id, alias ->
                if (borSocketModule.isConnected())
                    borSocketModule.disconnect()

                eventListener?.onBreakoutSaveMediaState()

                participantModule.changeMediaState(
                    internalDataModule.mainRoomId,
                    internalDataModule.companyId,
                    currentUserId,
                    KmeMediaStateType.LIVE_MEDIA,
                    KmeMediaDeviceState.DISABLED
                )

                internalDataModule.breakoutRoomId = id
                participantModule.updateParticipantsRoomId(true)
                eventListener?.onBreakoutRoomStart(id, alias, selfAssignedBorId == id)
                selfAssignedBorId = null
            }
        }
    }

    private fun handleLeaveRoom() {
        internalDataModule.breakoutRoomId = 0
        if (borSocketModule.isConnected())
            borSocketModule.disconnect()
        participantModule.updateParticipantsRoomId(true)
        eventListener?.onBreakoutRoomStop()
    }

    private fun handleAssignments(payload: BreakoutRoomState?) {
        payload?.assignments?.forEach { assignment ->
            borState?.assignments?.find { localAssignment ->
                localAssignment.userId == assignment.userId
            }?.let {
                it.breakoutRoomId = assignment.breakoutRoomId
                it.status = assignment.status
            } ?: run {
                borState?.assignments?.add(assignment)
            }
            payload.breakoutRooms.forEach { room ->
                borState?.breakoutRooms?.find { localRoom ->
                    localRoom.id == room.id
                }?.let {
                    it.participantsCount = room.participantsCount
                }
            }

            if (borState?.status == KmeBreakoutRoomStatusType.ACTIVE) {
                participantModule.updateParticipantsRoomId()
                if (assignment.userId == currentUserId) {
                    handleJoinRoom(payload)
                }
            }
        }

        payload?.removedAssignments?.forEach { assignment ->
            borState?.assignments?.removeAll { localAssignment ->
                localAssignment.userId == assignment.userId
            }
            payload.breakoutRooms.forEach { room ->
                borState?.breakoutRooms?.find { localRoom ->
                    localRoom.id == room.id
                }?.let {
                    it.participantsCount = room.participantsCount
                }
            }

            if (borState?.status == KmeBreakoutRoomStatusType.ACTIVE) {
                participantModule.updateParticipantsRoomId()
                if (assignment.userId == currentUserId) {
                    handleLeaveRoom()
                }
            }
        }

        eventListener?.onBreakoutRoomStateChanged()
    }

}
