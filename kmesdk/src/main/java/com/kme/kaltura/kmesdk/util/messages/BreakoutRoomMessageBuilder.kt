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
//        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        module = KmeMessageModule.BREAKOUT
        name = KmeMessageEvent.BREAKOUT_PASS_TO_MS
        type = KmeMessageEventType.VOID
        payload = BreakoutUserJoinedPayload(
            KmeMessageEvent.BREAKOUT_USER_JOINED,
            BreakoutUserJoinedData(userId.toString(), breakoutRoomId.toString()),
            roomId,
            companyId
        )
    }
}
