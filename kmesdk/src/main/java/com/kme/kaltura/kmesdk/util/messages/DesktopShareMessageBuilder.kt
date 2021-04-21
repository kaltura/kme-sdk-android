package com.kme.kaltura.kmesdk.util.messages

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEventType
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage.DesktopShareInitOnRoomInitPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage.UpdateDesktopShareStatePayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeConstraint

internal fun buildDesktopShareInitOnRoomInitMessage(): KmeDesktopShareModuleMessage<DesktopShareInitOnRoomInitPayload> {
    return KmeDesktopShareModuleMessage<DesktopShareInitOnRoomInitPayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        module = KmeMessageModule.DESKTOP_SHARE
        name = KmeMessageEvent.DESKTOP_SHARE_INIT_ON_ROOM_INIT
        type = KmeMessageEventType.CALLBACK
//        payload = DesktopShareInitOnRoomInitPayload(roomId, companyId)
    }
}

internal fun buildUpdateDesktopShareStateMessage(
    roomId: Long,
    userId: String,
    companyId: Long,
    isActive: Boolean,
): KmeDesktopShareModuleMessage<UpdateDesktopShareStatePayload> {
    return KmeDesktopShareModuleMessage<UpdateDesktopShareStatePayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        module = KmeMessageModule.DESKTOP_SHARE
        name = if (isActive) KmeMessageEvent.UPDATE_DESKTOP_SHARE_STATE else KmeMessageEvent.STOP_DESKTOP_SHARE
        type = KmeMessageEventType.VOID
        payload = UpdateDesktopShareStatePayload(roomId, companyId, userId, isActive)
    }
}
