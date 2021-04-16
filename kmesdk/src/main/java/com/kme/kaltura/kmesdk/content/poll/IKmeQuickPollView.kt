package com.kme.kaltura.kmesdk.content.poll

import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage

/**
 * An interface for QuickPoll events.
 */
interface IKmeQuickPollView {

    /**
     * Initialize function. Setting config.
     *
     * @param config sets initial data for the QuickPoll view.
     */
    fun init(config: KmeQuickPollView.Config)

    /**
     * Send an answer event.
     *
     * @param answer answer object that will be send.
     */
    fun sendAnswer(answer: KmeQuickPollModuleMessage.QuickPollPayload.Answer)

    /**
     * Shows a poll view with specific UI.
     *
     * @param payload object describes initial data for showing a poll view.
     */
    fun startPoll(payload: KmeQuickPollModuleMessage.QuickPollStartedPayload)

    /**
     * Hides the poll view or shows view with results.
     *
     * @param payload object contains data that needed to display the results.
     */
    fun endPoll(payload: KmeQuickPollModuleMessage.QuickPollEndedPayload)

    /**
     * Shows [KmeQuickPollResultsView].
     *
     * @param payload object contains data that needed to display the results.
     * @param answer answer object that can be applied when displaying [KmeQuickPollResultsView].
     * @param answers list of answers that can be applied when displaying [KmeQuickPollResultsView].
     */
    fun showResultsView(
        payload: KmeQuickPollModuleMessage.QuickPollEndedPayload? = null,
        answer: KmeQuickPollModuleMessage.QuickPollPayload.Answer? = null,
        answers: List<KmeQuickPollModuleMessage.QuickPollPayload.Answer>? = null,
    )

    /**
     * Shows [KmeQuickPollResultsView] and applies a list of answers.
     *
     * @param answers list of answers that will be applied.
     */
    fun applyResults(answers: List<KmeQuickPollModuleMessage.QuickPollPayload.Answer>?)

    /**
     * Shows [KmeQuickPollResultsView] and applies an answer object.
     *
     * @param answer answer object that will be applied.
     */
    fun applyResult(answer: KmeQuickPollModuleMessage.QuickPollPayload.Answer)

}