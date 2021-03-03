package com.kme.kaltura.kmesdk.content.poll

import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage

interface IKmeQuickPollResultsView {

    fun init(payload: KmeQuickPollModuleMessage.QuickPollEndedPayload)

}