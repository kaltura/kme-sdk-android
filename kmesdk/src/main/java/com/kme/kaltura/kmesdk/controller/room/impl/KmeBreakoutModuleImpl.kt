package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeBreakoutModule
import com.kme.kaltura.kmesdk.controller.room.IKmeBreakoutModule.IKmeBreakoutEvents
import com.kme.kaltura.kmesdk.controller.room.IKmeInternalDataModule
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.room.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.di.KmeKoinScope
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.ifNonNull
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.messages.buildAssignUserBorMessage
import com.kme.kaltura.kmesdk.util.messages.buildCallToInstructorMessage
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage.RoomModuleSettingsChangedPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeBreakoutRoomStatusType
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
    private val internalDataModule: IKmeInternalDataModule by scopedInject()
    private val userController: IKmeUserController by inject()

    private val currentUserId by lazy { userController.getCurrentUserInfo()?.getUserId() ?: 0 }

    private var borState: BreakoutRoomState? = null
    private var eventListener: IKmeBreakoutEvents? = null

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
            KmeMessageEvent.BREAKOUT_INSTRUCTOR_MESSAGE
        )
    }

    /**
     * Handle breakout room setting changes from main room
     */
    override fun onSettingChanged(payload: RoomModuleSettingsChangedPayload) {

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
        assignUserToBor(currentUserId, breakoutRoomId)
    }

    /**
     * Assign user to specific room
     */
    override fun assignUserToBor(
        userId: Long,
        breakoutRoomId: Long
    ) {
        mainRoomSocketModule.send(
            buildAssignUserBorMessage(
                internalDataModule.mainRoomId,
                internalDataModule.companyId,
                userId,
                breakoutRoomId
            )
        )
    }

    /**
     * Call instructor
     */
    override fun callToInstructor() {
        borState?.breakoutRooms?.find {
            it.id == internalDataModule.breakoutRoomId
        }?.let { room ->
            if (room.raisedHandUserId != null ||
                room.raisedHandUserId == currentUserId
            ) {
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
    }

    /**
     * Getting list of breakout rooms
     */
    override fun getBreakoutState() = borState

    /**
     * Listen for subscribed events
     */
    private val breakoutRoomMessageHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.MODULE_STATE -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomState>? = message.toType()
                    borState = msg?.payload
                    if (msg?.payload?.status == KmeBreakoutRoomStatusType.ACTIVE) {
                        handleJoinRoom(msg.payload)
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
                        borState?.breakoutRooms?.find { room->
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
                    msg?.payload?.breakoutRooms?.get(0)?.id?.let { callRoomId ->
                        borState?.breakoutRooms?.find { room ->
                            room.id == callRoomId
                        }?.let { breakoutRoom ->
                            val callUserId = msg.payload?.breakoutRooms?.get(0)?.raisedHandUserId
                            breakoutRoom.raisedHandUserId = callUserId
                            eventListener?.onBreakoutCallInstructor(callRoomId, callUserId)
                        }
                    }
                }
                KmeMessageEvent.BREAKOUT_INSTRUCTOR_MESSAGE -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutMessagePayload>? = message.toType()
                    ifNonNull(
                        msg?.payload?.messageMetadata?.senderId,
                        msg?.payload?.messageMetadata?.messageText
                    ) { userId, text ->
                        eventListener?.onBreakoutInstructorMessage(userId, text)
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
                eventListener?.onBreakoutRoomStart(id, alias)
            }
        }
    }

    private fun handleLeaveRoom() {
        if (borSocketModule.isConnected())
            borSocketModule.disconnect()
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
            if (assignment.userId == currentUserId &&
                borState?.status == KmeBreakoutRoomStatusType.ACTIVE
            ) {
                handleJoinRoom(payload)
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
            if (assignment.userId == currentUserId &&
                borState?.status == KmeBreakoutRoomStatusType.ACTIVE
            ) {
                handleLeaveRoom()
            }
        }

        eventListener?.onBreakoutRoomStateChanged()
    }

}
