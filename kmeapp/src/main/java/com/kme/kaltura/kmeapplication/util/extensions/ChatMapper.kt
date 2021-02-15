package com.kme.kaltura.kmeapplication.util.extensions

import com.kme.kaltura.kmeapplication.data.MappedChatMessage
import com.kme.kaltura.kmeapplication.data.MappedConversation
import com.kme.kaltura.kmeapplication.data.MappedUser
import com.kme.kaltura.kmeapplication.util.TimeUtil
import com.kme.kaltura.kmesdk.ws.message.chat.KmeChatMessage
import com.kme.kaltura.kmesdk.ws.message.chat.KmeConversation
import com.kme.kaltura.kmesdk.ws.message.module.KmeChatModuleMessage

fun List<KmeConversation>.mapConversations(timeUtil: TimeUtil): List<MappedConversation> {
    return map {
        val lastMessage: MappedChatMessage? = it.lastMessage?.mapMessage(timeUtil)

        val participants: MutableList<MappedUser> = mutableListOf()

        MappedConversation(
            it.id.toNonNull(),
            it.name.toNonNull(),
            it.avatar,
            participants,
            lastMessage,
            it.unreadMessages,
            it.conversationType
        ).apply {
            hasAccess = it.hasAccess
            isSystem = it.isSystem ?: false
        }
    }
}

fun KmeConversation.mapConversation(timeUtil: TimeUtil): MappedConversation {
    val lastMessage: MappedChatMessage? = this.lastMessage?.mapMessage(timeUtil)

    val participants: MutableList<MappedUser> = mutableListOf()

    return MappedConversation(
        this.id.toNonNull(),
        this.name.toNonNull(),
        this.avatar,
        participants,
        lastMessage,
        this.unreadMessages,
        this.conversationType
    ).apply {
        hasAccess = this@mapConversation.hasAccess
        isSystem = this@mapConversation.isSystem ?: false
    }
}

fun List<KmeChatMessage>.mapMessages(timeUtil: TimeUtil): List<MappedChatMessage> {
    return sortedByDescending { kmeChatMessage -> kmeChatMessage.timestamp }.map { chatMessage ->
        val mappedUser = MappedUser(chatMessage.user)
        val createdAt = timeUtil.toDate(chatMessage.timestamp ?: 0L)
        MappedChatMessage(
            mappedUser,
            chatMessage.id.toNonNull(),
            chatMessage.message.toNonNull(),
            createdAt,
            chatMessage.conversationId.toNonNull()
        ).apply {
            replyMessage = chatMessage.parsedMetadata?.let {
                val mappedReplyUser = MappedUser(it.user)
                val replyCreatedAt = timeUtil.toDate(it.timestamp ?: 0)

                MappedChatMessage(
                    mappedReplyUser,
                    it.id.toNonNull(),
                    it.message.toNonNull(),
                    replyCreatedAt,
                    it.conversationId.toNonNull()
                )
            }
        }
    }
}

fun KmeChatModuleMessage.ReceiveMessagePayload.mapMessage(timeUtil: TimeUtil): MappedChatMessage {
    val mappedUser = MappedUser(user)
    val createdAt = timeUtil.toDate(timestamp ?: 0L)
    return MappedChatMessage(
        mappedUser,
        id.toNonNull(),
        message.toNonNull(),
        createdAt,
        conversationId.toNonNull()
    ).apply {
        replyMessage = parsedMetadata?.let {
            val mappedReplyUser = MappedUser(it.user)
            val replyCreatedAt = timeUtil.toDate(it.timestamp ?: 0)

            MappedChatMessage(
                mappedReplyUser,
                it.id.toNonNull(),
                it.message.toNonNull(),
                replyCreatedAt,
                it.conversationId.toNonNull()
            )
        }
    }
}

fun KmeChatMessage.mapMessage(timeUtil: TimeUtil): MappedChatMessage {
    val mappedUser = MappedUser(user)
    val createdAt = timeUtil.toDate(timestamp ?: 0L)
    return MappedChatMessage(
        mappedUser,
        id.toNonNull(),
        message.toNonNull(),
        createdAt,
        conversationId.toNonNull()
    ).apply {
        replyMessage = parsedMetadata?.let {
            val mappedReplyUser = MappedUser(it.user)
            val replyCreatedAt = timeUtil.toDate(it.timestamp ?: 0)

            MappedChatMessage(
                mappedReplyUser,
                it.id.toNonNull(),
                it.message.toNonNull(),
                replyCreatedAt,
                it.conversationId.toNonNull()
            )
        }
    }
}