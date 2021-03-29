package com.kme.kaltura.kmesdk.content.poll

import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage

interface IKmeQuickPollView {

    fun init(config: KmeQuickPollView.Config)

    fun sendAnswer(answer: KmeQuickPollModuleMessage.QuickPollPayload.Answer)

    fun startPoll(payload: KmeQuickPollModuleMessage.QuickPollStartedPayload)

    fun endPoll(payload: KmeQuickPollModuleMessage.QuickPollEndedPayload)

    fun showResultsView(
        payload: KmeQuickPollModuleMessage.QuickPollEndedPayload? = null,
        answer: KmeQuickPollModuleMessage.QuickPollPayload.Answer? = null,
        answers: List<KmeQuickPollModuleMessage.QuickPollPayload.Answer>? = null,
    )

    fun applyResults(answers: List<KmeQuickPollModuleMessage.QuickPollPayload.Answer>?)

    fun applyResult(answer: KmeQuickPollModuleMessage.QuickPollPayload.Answer)

}