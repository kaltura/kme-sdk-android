package com.kme.kaltura.kmesdk.util.messages

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEventType
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.module.KmeVideoModuleMessage

internal fun buildGetPlayerStateMessage(
    roomId: Long,
    companyId: Long,
    messageModule: KmeMessageModule
): KmeVideoModuleMessage<KmeVideoModuleMessage.GetPlayerStatePayload> {
    return KmeVideoModuleMessage<KmeVideoModuleMessage.GetPlayerStatePayload>().apply {
        constraint = listOf()
        module = messageModule
        name = KmeMessageEvent.GET_PLAYER_STATE
        type = KmeMessageEventType.VOID
        payload =
            KmeVideoModuleMessage.GetPlayerStatePayload(roomId, companyId, explicitRequest = true)
    }
}