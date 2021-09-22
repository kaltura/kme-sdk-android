package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeBreakoutModule
import com.kme.kaltura.kmesdk.controller.room.IKmeBreakoutModule.IKmeBreakoutEvents
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.di.inject
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

    private val roomController: IKmeRoomController by controllersScope().inject()
    private val userController: IKmeUserController by inject()

    private val currentUserId by lazy { userController.getCurrentUserInfo()?.getUserId() ?: 0 }

    private var breakoutRooms: List<BreakoutRoom>? = mutableListOf()
    private var eventListener: IKmeBreakoutEvents? = null

    /**
     * Subscribing for the room events related breakout rooms
     */
    override fun subscribe() {
        roomController.listen(
            breakoutRoomMessageHandler,
            KmeMessageEvent.MODULE_STATE,
            KmeMessageEvent.BREAKOUT_START,
            KmeMessageEvent.BREAKOUT_STOP,
            KmeMessageEvent.BREAKOUT_ADD_ROOM,
            KmeMessageEvent.BREAKOUT_DELETE_ROOM,
            KmeMessageEvent.BREAKOUT_CHANGE_ROOM_NAME,
            KmeMessageEvent.BREAKOUT_ASSIGN_PARTICIPANTS,
            KmeMessageEvent.BREAKOUT_RESHUFFLE_ASSIGNMENTS,
            KmeMessageEvent.BREAKOUT_MODERATOR_JOINED,
            KmeMessageEvent.BREAKOUT_USER_JOINED,
            KmeMessageEvent.BREAKOUT_EXTEND_TIME_LIMIT,
            KmeMessageEvent.BREAKOUT_CALL_TO_INSTRUCTOR,
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
                    breakoutRooms = msg?.payload?.breakoutRooms

                    if (msg?.payload?.status == KmeBreakoutRoomStatusType.ACTIVE) {
                        handleBreakoutStart(msg)
                    }
                }
                KmeMessageEvent.BREAKOUT_START -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    handleBreakoutStart(msg)
                }
                KmeMessageEvent.BREAKOUT_STOP -> {
//                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    eventListener?.onBreakoutRoomStop()
                }
                KmeMessageEvent.BREAKOUT_ADD_ROOM -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutAddRoomPayload>? = message.toType()
                    val test = ""
                }
                KmeMessageEvent.BREAKOUT_DELETE_ROOM -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    val test = ""
                }
                KmeMessageEvent.BREAKOUT_CHANGE_ROOM_NAME -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutChangeRoomNamePayload>? =
                        message.toType()
                    val test = ""
                }
                KmeMessageEvent.BREAKOUT_ASSIGN_PARTICIPANTS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    val test = ""
                }
                KmeMessageEvent.BREAKOUT_RESHUFFLE_ASSIGNMENTS -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    val test = ""
                }
                KmeMessageEvent.BREAKOUT_MODERATOR_JOINED -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    val test = ""
                }
                KmeMessageEvent.BREAKOUT_USER_JOINED -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    val test = ""
                }
                KmeMessageEvent.BREAKOUT_EXTEND_TIME_LIMIT -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutExtendTimePayload>? = message.toType()
                    val test = ""
                }
                KmeMessageEvent.BREAKOUT_CALL_TO_INSTRUCTOR -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    val test = ""
                }
                KmeMessageEvent.BREAKOUT_INSTRUCTOR_MESSAGE -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    val test = ""
                }
                else -> {
                }
            }
        }
    }

    private fun handleBreakoutStart(msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>?) {
        breakoutRooms?.find { room ->
            room.id == msg?.payload?.assignments?.find { assignment ->
                assignment.userId == currentUserId
            }?.breakoutRoomId
        }?.let { breakoutRoom ->
            ifNonNull(breakoutRoom.id, breakoutRoom.alias) { id, alias ->
                eventListener?.onBreakoutRoomStart(id, alias)
            }
        }
    }

}
