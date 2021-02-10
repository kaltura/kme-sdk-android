package com.kme.kaltura.kmeapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmeapplication.data.ChatMessageStatus
import com.kme.kaltura.kmeapplication.data.MappedChatMessage
import com.kme.kaltura.kmeapplication.data.MappedUser
import com.kme.kaltura.kmeapplication.util.TimeUtil
import com.kme.kaltura.kmeapplication.util.extensions.*
import com.kme.kaltura.kmeapplication.util.toSingleEvent
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeChatModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeChatModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.type.KmeLoadType
import java.util.*

class ChatViewModel(
    private val kmeSdk: KME,
    private val timeUtil: TimeUtil
) : ViewModel() {

    private val messagesUpdates = MutableLiveData<List<MappedChatMessage>>()
    val messagesUpdatesLiveData = messagesUpdates.toSingleEvent()

    private val newMessage = MutableLiveData<MappedChatMessage>()
    val newMessageLiveData = newMessage.toSingleEvent()

    private val updateMessage = MutableLiveData<Pair<String, MappedChatMessage>>()
    val updateMessageLiveData = updateMessage.toSingleEvent()

    private val deleteMessage = MutableLiveData<String>()
    val deleteMessageLiveData = deleteMessage.toSingleEvent()

    val currentParticipant: KmeParticipant? by lazy { kmeSdk.userController.getCurrentParticipant() }

    private val userInfo by lazy { kmeSdk.userController.getCurrentUserInfo() }
    private val loadedMessages = mutableListOf<MappedChatMessage>()
    private var openedConversationId: String? = null

    var allowLoadMore = true
        private set

    fun subscribe() {
        kmeSdk.roomController.listen(
            messagesHandler,
            KmeMessageEvent.LOAD_MESSAGES,
            KmeMessageEvent.RECEIVE_MESSAGE,
            KmeMessageEvent.DELETED_MESSAGE
        )
    }

    fun getCurrentUserId() = userInfo?.getUserId() ?: 0L

    fun buildSelfMessage(
        conversationId: String,
        message: CharSequence,
        replyMessage: MappedChatMessage? = null
    ): MappedChatMessage {
        val mappedUser = MappedUser(userInfo)
        val createdAt = timeUtil.currentDate()

        var mappedMessage = MappedChatMessage(
            mappedUser,
            UUID.randomUUID().toString(),
            message.toString(),
            createdAt,
            conversationId,
            ChatMessageStatus.SENDING,
            replyMessage
        )

        mappedMessage = handleItemStyle(RECEIVED_MESSAGE_POSITION, mappedMessage, loadedMessages)
        loadedMessages.add(0, mappedMessage)

        return mappedMessage
    }

    fun send(
        conversationId: String,
        roomId: Long,
        companyId: Long,
        message: CharSequence,
        replyMessage: MappedChatMessage? = null
    ) {
        if (replyMessage == null) {
            kmeSdk.roomController.chatModule.sendMessage(
                roomId,
                companyId,
                conversationId,
                message.toString(),
                userInfo
            )
        } else {
            kmeSdk.roomController.chatModule.replyMessage(
                roomId,
                companyId,
                conversationId,
                message.toString(),
                replyMessage.asKmeMessage(),
                userInfo,
            )
        }
        updateMessageStyle(1)
    }

    fun loadMessages(
        conversationId: String,
        roomId: Long,
        companyId: Long,
        fromMessageId: String? = null
    ) {
        this.openedConversationId = conversationId

        if (fromMessageId == null) {
            allowLoadMore = true
            loadedMessages.clear()
            kmeSdk.roomController.chatModule.loadMessages(
                roomId,
                companyId,
                conversationId,
            )
        } else {
            kmeSdk.roomController.chatModule.loadMessages(
                roomId,
                companyId,
                conversationId,
                KmeLoadType.PARTIAL_LOAD,
                fromMessageId
            )
        }
    }

    private fun updateMessageStyle(index: Int) {
        if (index >= 0 && index < loadedMessages.size) {
            var message = loadedMessages[index]
            message = handleItemStyle(index, message, loadedMessages)
            updateMessage.value = Pair(message.id, message)
        }
    }

    fun deleteMessage(
        messageId: String,
        conversationId: String,
        roomId: Long,
        companyId: Long
    ) {
        kmeSdk.roomController.chatModule.deleteMessage(roomId, companyId, conversationId, messageId)
    }

    fun getLastLoadedMessage(): MappedChatMessage? {
        return messagesUpdates.value?.last()
    }

    fun startPrivateChat(
        targetUserId: Long,
        roomId: Long,
        companyId: Long
    ) {
        kmeSdk.roomController.chatModule.startPrivateChat(roomId, companyId, targetUserId)
    }

    private val messagesHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.LOAD_MESSAGES -> {
                    val loadMessagesMessage: KmeChatModuleMessage<LoadMessagesPayload>? =
                        message.toType()

                    val mappedMessages = loadMessagesMessage?.payload?.messages
                        ?.mapMessages(timeUtil)
                        ?.filter { !loadedMessages.contains(it) }

                    if (mappedMessages.isNullOrEmpty()) {
                        allowLoadMore = false
                    } else {
                        mappedMessages.updateChatItemsStyle()
                        loadedMessages.addAll(mappedMessages)
                        messagesUpdates.value = mappedMessages
                    }
                }
                KmeMessageEvent.RECEIVE_MESSAGE -> {
                    val receivedMessage: KmeChatModuleMessage<ReceiveMessagePayload>? =
                        message.toType()

                    val mappedMessage = receivedMessage?.payload?.mapMessage(timeUtil)
                    mappedMessage?.let {
                        handleReceiveMessage(it)
                    }
                }
                KmeMessageEvent.DELETED_MESSAGE -> {
                    val deleteMsg: KmeChatModuleMessage<DeleteMessagePayload>? =
                        message.toType()

                    deleteMsg?.payload?.messageId?.let {
                        handleRemoveMessage(it)
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun handleReceiveMessage(message: MappedChatMessage) {
        val foundMessage =
            loadedMessages.find { mappedChatMessage ->
                (mappedChatMessage.conversationId == message.conversationId
                        && mappedChatMessage.id == message.id)
                        || (mappedChatMessage.text == message.text
                        && (mappedChatMessage.status != null && mappedChatMessage.status == message.status
                        || (mappedChatMessage.status == ChatMessageStatus.SENDING && message.status == null))
                        && mappedChatMessage.user.id == message.user.id)
            }

        if (foundMessage == null) {
            val updatedMessage = handleItemStyle(RECEIVED_MESSAGE_POSITION, message, loadedMessages)
            loadedMessages.add(0, updatedMessage)
            updateMessageStyle(1)
            newMessage.value = updatedMessage
        } else {
            val indexOf = loadedMessages.indexOf(foundMessage)
            message.status = ChatMessageStatus.SENT
            val updatedMessage = handleItemStyle(indexOf, message, loadedMessages)
            updateMessage.value = Pair(foundMessage.id, updatedMessage)
            loadedMessages[indexOf] = updatedMessage
        }
    }

    private fun handleRemoveMessage(messageId: String) {
        val foundMessage = loadedMessages.find { message -> message.id == messageId }

        if (foundMessage != null) {
            val indexOf = loadedMessages.indexOf(foundMessage)
            loadedMessages.remove(foundMessage)
            deleteMessage.value = messageId

            updateMessageStyle(indexOf)
            updateMessageStyle(indexOf - 1)
        }
    }

    fun onConversationClosed() {
        openedConversationId = null
        loadedMessages.clear()
    }

    override fun onCleared() {
        super.onCleared()
        loadedMessages.clear()
    }

}
