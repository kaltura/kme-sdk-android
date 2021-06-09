package com.kme.kaltura.kmesdk.util.messages

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEventType
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage.StartDesktopSharePayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeConstraint
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType

internal fun buildSetActiveContentMessage(
    userId: Long?,
    contentType: KmeContentType
): KmeDesktopShareModuleMessage<StartDesktopSharePayload> {
    return KmeDesktopShareModuleMessage<StartDesktopSharePayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        module = KmeMessageModule.ACTIVE_CONTENT
        name = KmeMessageEvent.SET_ACTIVE_CONTENT
        type = KmeMessageEventType.VOID
        payload = StartDesktopSharePayload(
            KmeDesktopShareModuleMessage.DesktopShareMetadata(
                userId,
                when (contentType) {
                    KmeContentType.CONFERENCE_VIEW -> true
                    else -> null
                }
            ), contentType
        )
    }
}
