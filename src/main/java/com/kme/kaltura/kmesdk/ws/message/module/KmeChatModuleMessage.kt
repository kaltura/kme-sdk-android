package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserInfoData
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.chat.KmeChatMessage
import com.kme.kaltura.kmesdk.ws.message.chat.KmeConversation
import com.kme.kaltura.kmesdk.ws.message.type.KmeLoadType

class KmeChatModuleMessage<T : KmeChatModuleMessage.ChatPayload> :
    KmeMessage<T>() {

    data class LoadConversationPayload(
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null
    ) : ChatPayload()

    data class ReceiveConversationsPayload(
        @SerializedName("conversations") val conversations: List<KmeConversation>? = null,
    ) : ChatPayload()

    data class LoadMessagesPayload(
        @SerializedName("conversation_id") val conversationId: String? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null
    ) : ChatPayload() {
        @SerializedName("load_type")
        var loadType: KmeLoadType? = null
        @SerializedName("from_date")
        var fromMessageId: String? = null
        @SerializedName("messages")
        val messages: List<KmeChatMessage>? = null
    }

    data class SendMessagePayload(
        @SerializedName("conversation_id") var conversationId: String? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("message") var message: String? = null,
        @SerializedName("user") var user: User? = null
    ) : ChatPayload() {

        data class User(
            @SerializedName("id") var userId: Long? = null,
            @SerializedName("name") var name: String? = null,
        )

    }

    data class ReceiveMessagePayload(
        @SerializedName("id") val id: String? = null,
        @SerializedName("conversation_id") val conversationId: String? = null,
        @SerializedName("message") val message: String? = null,
        @SerializedName("message_metadata") val metadata: String? = null,
        @SerializedName("timestamp") val timestamp: Long? = null,
        @SerializedName("user") val user: KmeUserInfoData? = null
    ) : ChatPayload()

    open class ChatPayload : Payload()
}