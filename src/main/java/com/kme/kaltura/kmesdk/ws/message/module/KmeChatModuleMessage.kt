package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.chat.KmeChatMessage
import com.kme.kaltura.kmesdk.ws.message.chat.KmeConversation

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
        @SerializedName("messages")
        val messages: List<KmeChatMessage>? = null
    }

    open class ChatPayload : Payload()
}