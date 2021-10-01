package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeBreakoutModule
import com.kme.kaltura.kmesdk.controller.room.IKmeBreakoutModule.IKmeBreakoutEvents
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.room.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.di.KmeKoinScope
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.ifNonNull
import com.kme.kaltura.kmesdk.toType
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
    private val userController: IKmeUserController by inject()

    private val currentUserId by lazy { userController.getCurrentUserInfo()?.getUserId() ?: 0 }

    private var breakoutRooms: MutableList<BreakoutRoom> = mutableListOf()
    private var borState: BreakoutRoomStatusPayload? = null
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

    override fun onSettingChanged(payload: RoomModuleSettingsChangedPayload) {

    }

    override fun setEventsListener(listener: IKmeBreakoutEvents) {
        eventListener = listener
    }

    /**
     * Listen for subscribed events
     */
    private val breakoutRoomMessageHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.MODULE_STATE -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    borState = msg?.payload
                    borState?.breakoutRooms?.let {
                        breakoutRooms.addAll(it)
                    }

                    if (msg?.payload?.status == KmeBreakoutRoomStatusType.ACTIVE) {
                        handleJoinRoom(msg)
                    }
                }
                KmeMessageEvent.BREAKOUT_START_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    borState = msg?.payload

                    handleJoinRoom(msg)
                }
                KmeMessageEvent.BREAKOUT_STOP_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    // TODO: handle removed assignments for moderator
                    borState = null
                    handleLeaveRoom()
                }
                KmeMessageEvent.BREAKOUT_ADD_ROOM_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutAddRoomPayload>? = message.toType()
                    msg?.payload?.room?.let {
                        breakoutRooms.add(it)
                    }
                }
                KmeMessageEvent.BREAKOUT_DELETE_ROOM_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    msg?.payload?.breakoutRooms?.let {
                        breakoutRooms.clear()
                        breakoutRooms.addAll(it)
                    }
                    msg?.payload?.removedAssignments?.let { assignments ->
                        if (assignments.isNullOrEmpty()) return
                        // TODO: handle removed assignments for moderator
                    }
                }
                KmeMessageEvent.BREAKOUT_CHANGE_ROOM_NAME_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutChangeNamePayload>? = message.toType()
                    breakoutRooms.find { room ->
                        room.id == msg?.payload?.room?.id
                    }?.let {
                        it.name = msg?.payload?.room?.name
                    }
                }
                KmeMessageEvent.BREAKOUT_ASSIGN_PARTICIPANTS_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    if (borState?.status == KmeBreakoutRoomStatusType.ACTIVE) {
                        msg?.payload?.removedAssignments?.find { assignment ->
                            assignment.userId == currentUserId
                        }?.let {
                            handleLeaveRoom()
                        } ?: run {
                            handleJoinRoom(msg)
                        }
                    }
                }
                KmeMessageEvent.BREAKOUT_MOVE_TO_NEXT_ROOM,
                KmeMessageEvent.BREAKOUT_RESHUFFLE_ASSIGNMENTS_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    if (borState?.status == KmeBreakoutRoomStatusType.ACTIVE) {
                        msg?.payload?.assignments?.find { assignment ->
                            assignment.userId == currentUserId
                        }?.let {
                            handleJoinRoom(msg)
                        }
                    }
                }
                KmeMessageEvent.BREAKOUT_CLEAR_ASSIGNMENTS_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    // TODO: handle removed assignments for moderator
                }
                KmeMessageEvent.BREAKOUT_MODERATOR_JOINED_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                }
                KmeMessageEvent.BREAKOUT_USER_JOINED_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                }
                KmeMessageEvent.BREAKOUT_EXTEND_TIME_LIMIT_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutExtendTimePayload>? = message.toType()
                }
                KmeMessageEvent.BREAKOUT_CALL_TO_INSTRUCTOR_SUCCESS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                }
                KmeMessageEvent.BREAKOUT_INSTRUCTOR_MESSAGE -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                }
                else -> {
                }
            }
        }
    }

    private fun handleJoinRoom(msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>?) {
        breakoutRooms.find { room ->
            room.id == msg?.payload?.assignments?.find { assignment ->
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

}
