package com.kme.kaltura.kmesdk.util.messages

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEventType
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.module.KmeXLRoomModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeXLRoomModuleMessage.XLRoomGetStatePayload

internal fun buildXlRoomGetStateMessage(
    roomId: Long,
    companyId: Long
): KmeXLRoomModuleMessage<XLRoomGetStatePayload> {
    return KmeXLRoomModuleMessage<XLRoomGetStatePayload>().apply {
        module = KmeMessageModule.XL_ROOM
        name = KmeMessageEvent.GET_MODULE_STATE
        type = KmeMessageEventType.VOID
        payload = XLRoomGetStatePayload(roomId, companyId)
    }
}
