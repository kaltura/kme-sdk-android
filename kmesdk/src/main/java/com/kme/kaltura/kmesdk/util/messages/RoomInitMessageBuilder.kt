package com.kme.kaltura.kmesdk.util.messages

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEventType
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage.JoinRoomPayload

internal fun buildJoinRoomMessage(
    roomId: Long,
    companyId: Long
): KmeRoomInitModuleMessage<JoinRoomPayload> {
    return KmeRoomInitModuleMessage<JoinRoomPayload>().apply {
        module = KmeMessageModule.ROOM_INIT
        name = KmeMessageEvent.JOIN_ROOM
        type = KmeMessageEventType.CALLBACK
        payload = JoinRoomPayload("load", roomId, companyId)
    }
}
