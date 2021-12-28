package com.kme.kaltura.kmesdk.util.messages

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEventType
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeConstraint

internal fun buildGetQuickPollStateMessage(
    roomId: Long,
    companyId: Long
): KmeQuickPollModuleMessage<QuickPollGetStatePayload> {
    return KmeQuickPollModuleMessage<QuickPollGetStatePayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        module = KmeMessageModule.QUICK_POLL
        name = KmeMessageEvent.GET_MODULE_STATE
        type = KmeMessageEventType.VOID
        payload = QuickPollGetStatePayload(roomId, companyId)
    }
}

internal fun buildSendQuickPollAnswerMessage(
    answer: QuickPollPayload.Answer,
    roomId: Long,
    companyId: Long,
): KmeQuickPollModuleMessage<QuickPollSendAnswerPayload> {
    return KmeQuickPollModuleMessage<QuickPollSendAnswerPayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        module = KmeMessageModule.QUICK_POLL
        name = KmeMessageEvent.QUICK_POLL_SEND_ANSWER
        type = KmeMessageEventType.VOID
        payload = QuickPollSendAnswerPayload(answer, roomId, companyId)
    }
}
