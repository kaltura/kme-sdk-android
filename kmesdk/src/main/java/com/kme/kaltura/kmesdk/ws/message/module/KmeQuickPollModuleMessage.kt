package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollAudienceType
import com.kme.kaltura.kmesdk.ws.message.type.KmeQuickPollType

class KmeQuickPollModuleMessage<T : KmeQuickPollModuleMessage.QuickPollPayload> :
    KmeMessage<T>() {

    data class GetQuickPollStatePayload(
        @SerializedName("room_id") val roomId: Long,
        @SerializedName("company_id") val companyId: Long,
    ) : QuickPollPayload()

    data class QuickPollAnswersStatePayload(
        @SerializedName("answers") val answers: List<Answer>?
    ) : QuickPollPayload()

    data class QuickPollStartedPayload(
        @SerializedName("creator_id") val creatorId: Long?,
        @SerializedName("is_anon") val isAnonymous: Boolean?,
        @SerializedName("name") val name: String?,
        @SerializedName("poll_id") val pollId: String?,
        @SerializedName("poll_status") val status: String?,
        @SerializedName("poll_type") val type: KmeQuickPollType?,
        @SerializedName("target_audience") val targetAudience: KmeQuickPollAudienceType?,
        @SerializedName("user_count") val userCount: Int?
    ) : QuickPollPayload()

    data class QuickPollEndedPayload(
        @SerializedName("poll_id") val pollId: String?,
        @SerializedName("answers") val answers: List<Answer>?,
        @SerializedName("should_present") val shouldPresent: Boolean?,
    ) : QuickPollPayload()

    data class QuickPollUserAnsweredPayload(
        @SerializedName("poll_id") val pollId: String?,
        @SerializedName("answer") val answer: Int?,
        @SerializedName("user_id") val userId: Long?,
    ) : QuickPollPayload()

    data class QuickPollSendAnswerPayload(
        @SerializedName("data") val answer: Answer?,
        @SerializedName("room_id") val roomId: Long?,
        @SerializedName("company_id") val companyId: Long?,
        @SerializedName("eventName") private val eventName: String = "answerPoll"
    ) : QuickPollPayload()

    open class QuickPollPayload : Payload() {

        data class Answer(
            @SerializedName("answer") val answer: Int?,
            @SerializedName("poll_id") val pollId: String?,
            @SerializedName("user_id") val userId: Long? = null,
        )

    }

}
