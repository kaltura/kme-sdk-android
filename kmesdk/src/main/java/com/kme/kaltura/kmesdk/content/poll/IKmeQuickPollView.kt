package com.kme.kaltura.kmesdk.content.poll

import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage

interface IKmeQuickPollView {

    fun init(config: KmeQuickPollView.Config)

    fun sendAnswer(answer: KmeQuickPollModuleMessage.QuickPollPayload.Answer)

    fun startPoll(payload: KmeQuickPollModuleMessage.QuickPollStartedPayload)

    fun endPoll(payload: KmeQuickPollModuleMessage.QuickPollEndedPayload)

    fun showResults(payload: KmeQuickPollModuleMessage.QuickPollEndedPayload)

}