package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeBreakoutModule
import com.kme.kaltura.kmesdk.controller.room.IKmeBreakoutModule.IKmeBreakoutEvents
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.KmeMessageManager
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage.RoomModuleSettingsChangedPayload
import org.koin.core.inject

/**
 * An implementation for actions with breakout rooms
 */
class KmeBreakoutModuleImpl : KmeController(), IKmeBreakoutModule {

    private val messageManager: KmeMessageManager by inject()

    private var eventListener: IKmeBreakoutEvents? = null

    /**
     * Subscribing for the room events related breakout rooms
     */
    override fun subscribe() {
        messageManager.listen(
            breakoutRoomMessageHandler,
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
            KmeMessageEvent.BREAKOUT_CALL_TO_INSTRUCTOR
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
                KmeMessageEvent.BREAKOUT_START -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    val test = ""

                    eventListener?.onBreakoutRoomChanged(0)
                }
                KmeMessageEvent.BREAKOUT_STOP -> {
                    val msg: KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>? = message.toType()
                    val test = ""
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

                    eventListener?.onBreakoutRoomChanged(0)
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
                else -> {
                }
            }
        }
    }

}
