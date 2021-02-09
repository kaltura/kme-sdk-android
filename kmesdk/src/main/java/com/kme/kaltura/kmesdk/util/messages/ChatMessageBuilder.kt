package com.kme.kaltura.kmesdk.util.messages

import com.kme.kaltura.kmesdk.rest.response.user.KmeUserInfoData
import com.kme.kaltura.kmesdk.toNonNull
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEventType
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.chat.KmeChatMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeChatModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeChatModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeConstraint
import com.kme.kaltura.kmesdk.ws.message.type.KmeLoadType

fun buildLoadConversationsMessage(
    roomId: Long,
    companyId: Long
): KmeChatModuleMessage<LoadConversationPayload> {
    return KmeChatModuleMessage<LoadConversationPayload>().apply {
        module = KmeMessageModule.CHAT
        name = KmeMessageEvent.LOAD_CONVERSATIONS
        type = KmeMessageEventType.CALLBACK
        payload = LoadConversationPayload(roomId, companyId)
    }
}

fun buildLoadMessagesMessage(
    roomId: Long,
    companyId: Long,
    conversationId: String,
    loadType: KmeLoadType = KmeLoadType.INITIAL_LOAD,
    fromMessageId: String? = null
): KmeChatModuleMessage<LoadMessagesPayload> {
    return KmeChatModuleMessage<LoadMessagesPayload>().apply {
        module = KmeMessageModule.CHAT
        name = KmeMessageEvent.LOAD_MESSAGES
        type = KmeMessageEventType.CALLBACK
        payload = LoadMessagesPayload(conversationId, roomId, companyId).apply {
            this.loadType = loadType
            this.fromMessageId = fromMessageId
        }
    }
}

fun buildSendMessage(
    roomId: Long,
    companyId: Long,
    conversationId: String,
    message: String,
    userInfo: KmeUserInfoData?
): KmeChatModuleMessage<SendMessagePayload> {
    return KmeChatModuleMessage<SendMessagePayload>().apply {
        module = KmeMessageModule.CHAT
        name = KmeMessageEvent.SEND_MESSAGE
        type = KmeMessageEventType.VOID
        payload = SendMessagePayload(
            conversationId,
            roomId,
            companyId,
            message
        ).apply {
            user = SendMessagePayload.User().apply {
                userId = userInfo?.getUserId()
                name = "${userInfo?.firstName.toNonNull()} ${userInfo?.lastName.toNonNull()}"
            }
        }
    }
}

fun buildReplyMessage(
    roomId: Long,
    companyId: Long,
    conversationId: String,
    message: String,
    replyMessage: KmeChatMessage,
    userInfo: KmeUserInfoData?
): KmeChatModuleMessage<SendMessagePayload> {
    return KmeChatModuleMessage<SendMessagePayload>().apply {
        module = KmeMessageModule.CHAT
        name = KmeMessageEvent.SEND_MESSAGE
        type = KmeMessageEventType.VOID
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        payload = SendMessagePayload().apply {
            this.conversationId = conversationId
            this.roomId = roomId
            this.companyId = companyId
            this.message = message
            this.metadata = replyMessage
            user = SendMessagePayload.User().apply {
                userId = userInfo?.getUserId()
                name = "${userInfo?.firstName.toNonNull()} ${userInfo?.lastName.toNonNull()}"
                avatar = userInfo?.avatar
            }
        }
    }
}

fun buildDeleteMessage(
    roomId: Long,
    companyId: Long,
    conversationId: String,
    messageId: String,
): KmeChatModuleMessage<DeleteMessagePayload> {
    return KmeChatModuleMessage<DeleteMessagePayload>().apply {
        module = KmeMessageModule.CHAT
        name = KmeMessageEvent.DELETE_MESSAGE
        type = KmeMessageEventType.BROADCAST
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        payload = DeleteMessagePayload().apply {
            this.conversationId = conversationId
            this.messageId = messageId
            this.roomId = roomId
            this.companyId = companyId
            this.ackId = "del_message_${conversationId}_${messageId}"
        }
    }
}

fun buildStartPrivateChatMessage(
    roomId: Long,
    companyId: Long,
    targetUserId: Long
): KmeChatModuleMessage<CreateDmConversationPayload> {
    return KmeChatModuleMessage<CreateDmConversationPayload>().apply {
        type = KmeMessageEventType.CALLBACK
        module = KmeMessageModule.CHAT
        name = KmeMessageEvent.CREATE_DM_CONVERSATION
        payload = CreateDmConversationPayload(
            targetUserId, roomId, companyId
        )
    }
}

fun buildGetConversationMessage(
    roomId: Long,
    companyId: Long,
    conversationId: String
): KmeChatModuleMessage<GetConversationPayload> {
    return KmeChatModuleMessage<GetConversationPayload>().apply {
        type = KmeMessageEventType.CALLBACK
        module = KmeMessageModule.CHAT
        name = KmeMessageEvent.GET_CONVERSATION
        payload = GetConversationPayload(
            conversationId, roomId, companyId
        )
    }
}
