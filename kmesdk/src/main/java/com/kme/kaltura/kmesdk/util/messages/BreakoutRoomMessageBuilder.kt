package com.kme.kaltura.kmesdk.util.messages

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEventType
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeConstraint

internal fun buildGetBreakoutStateMessage(
    roomId: Long,
    companyId: Long
): KmeBreakoutModuleMessage<GetBreakoutStatePayload> {
    return KmeBreakoutModuleMessage<GetBreakoutStatePayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        module = KmeMessageModule.BREAKOUT
        name = KmeMessageEvent.GET_MODULE_STATE
        type = KmeMessageEventType.VOID
        payload = GetBreakoutStatePayload(roomId, companyId)
    }
}

internal fun buildJoinBorMessage(
    roomId: Long?,
    companyId: Long?,
    userId: Long?,
    breakoutRoomId: Long?
): KmeBreakoutModuleMessage<BreakoutUserJoinedPayload> {
    return KmeBreakoutModuleMessage<BreakoutUserJoinedPayload>().apply {
        constraint = listOf()
        module = KmeMessageModule.BREAKOUT
        name = KmeMessageEvent.BREAKOUT_PASS_TO_MS
        type = KmeMessageEventType.VOID
        payload = BreakoutUserJoinedPayload(
            KmeMessageEvent.BREAKOUT_USER_JOINED,
            roomId,
            companyId,
            BreakoutEventBaseData(userId.toString(), breakoutRoomId.toString())
        )
    }
}

internal fun buildAssignUserBorMessage(
    roomId: Long?,
    companyId: Long?,
    userId: Long?,
    breakoutRoomId: Long?
): KmeBreakoutModuleMessage<BreakoutAssignUserPayload> {
    return KmeBreakoutModuleMessage<BreakoutAssignUserPayload>().apply {
        constraint = listOf()
        module = KmeMessageModule.BREAKOUT
        name = KmeMessageEvent.BREAKOUT_PASS_TO_MS
        type = KmeMessageEventType.VOID
        payload = BreakoutAssignUserPayload(
            KmeMessageEvent.BREAKOUT_ASSIGN_PARTICIPANTS,
            roomId,
            companyId,
            BreakoutAssignmentsData(listOf(BreakoutRoomAssignment(userId, breakoutRoomId))),
        )
    }
}

internal fun buildCallToInstructorMessage(
    roomId: Long?,
    companyId: Long?,
    userId: Long?,
    breakoutRoomId: Long?
): KmeBreakoutModuleMessage<BreakoutUserJoinedPayload> {
    return KmeBreakoutModuleMessage<BreakoutUserJoinedPayload>().apply {
        constraint = listOf()
        module = KmeMessageModule.BREAKOUT
        name = KmeMessageEvent.BREAKOUT_PASS_TO_MS
        type = KmeMessageEventType.VOID
        payload = BreakoutUserJoinedPayload(
            KmeMessageEvent.BREAKOUT_CALL_TO_INSTRUCTOR,
            roomId,
            companyId,
            BreakoutEventBaseData(userId.toString(), breakoutRoomId.toString()),
        )
    }
}
