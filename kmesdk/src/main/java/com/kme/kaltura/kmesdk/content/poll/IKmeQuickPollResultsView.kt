package com.kme.kaltura.kmesdk.content.poll

import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage

interface IKmeQuickPollResultsView {

    fun init(
        currentPollPayload: KmeQuickPollModuleMessage.QuickPollStartedPayload,
        endPollPayload: KmeQuickPollModuleMessage.QuickPollEndedPayload?
    )

    fun applyAnswer(
        answer: KmeQuickPollModuleMessage.QuickPollPayload.Answer
    )

    fun applyAnswers(
        answers: List<KmeQuickPollModuleMessage.QuickPollPayload.Answer>?
    )

}