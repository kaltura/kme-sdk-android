package com.kme.kaltura.kmesdk.content.poll

import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage

/**
 * An interface for viewing QuickPoll results.
 */
interface IKmeQuickPollResultsView {

    /**
     * Initialize function.
     *
     * @param currentPollPayload object describes initial data for showing a result view.
     * @param endPollPayload object contains data that needed to display the results.
     */
    fun init(
        currentPollPayload: KmeQuickPollModuleMessage.QuickPollStartedPayload,
        endPollPayload: KmeQuickPollModuleMessage.QuickPollEndedPayload?
    )

    /**
     * Applies an answer object to [KmeQuickPollResultsView].
     *
     * @param answer answer object that will be applied.
     */
    fun applyAnswer(
        answer: KmeQuickPollModuleMessage.QuickPollPayload.Answer
    )

    /**
     * Applies a list of answers.
     *
     * @param answers list of answers that will be applied.
     */
    fun applyAnswers(
        answers: List<KmeQuickPollModuleMessage.QuickPollPayload.Answer>?
    )

}