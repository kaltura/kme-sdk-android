package com.kme.kaltura.kmeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmeapplication.data.MappedChatMessage
import com.kme.kaltura.kmeapplication.data.MappedConversation
import com.kme.kaltura.kmeapplication.util.TimeUtil
import com.kme.kaltura.kmeapplication.util.extensions.isModerator
import com.kme.kaltura.kmeapplication.util.extensions.mapConversation
import com.kme.kaltura.kmeapplication.util.extensions.mapConversations
import com.kme.kaltura.kmeapplication.util.toSingleEvent
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeChatModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeChatModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeConversationType
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

class ConversationsViewModel(
    private val kmeSdk: KME,
    private val timeUtil: TimeUtil
) : ViewModel() {

    private val conversations = MutableLiveData<List<MappedConversation>>()
    val conversationsLiveData
        get() = conversations as LiveData<List<MappedConversation>>

    private val conversationChanged = MutableLiveData<MappedConversation>()
    val conversationChangedLiveData
        get() = conversationChanged as LiveData<MappedConversation>

    private val openConversation = MutableLiveData<MappedConversation>()
    val openConversationLiveData = openConversation.toSingleEvent()

    private val closeConversation = MutableLiveData<MappedConversation?>()
    val closeConversationLiveData = closeConversation.toSingleEvent()

    private val allUnreadMessageCounter = MutableLiveData<Int>()
    val allUnreadMessageCounterLiveData = allUnreadMessageCounter.toSingleEvent()

    private val conversationsList = mutableListOf<MappedConversation>()
    private val privateChatsList = mutableListOf<MappedConversation>()

    private var openedConversation: MappedConversation? = null

    private var roomId: Long = 0L
    private var companyId: Long = 0L

    fun loadConversations(roomId: Long, companyId: Long) {
        this.roomId = roomId
        this.companyId = companyId

        kmeSdk.roomController.listen(
            startPrivateChatHandler,
            KmeMessageEvent.CREATED_DM_CONVERSATION
        )
        kmeSdk.roomController.listen(
            messageHandler,
            KmeMessageEvent.RECEIVE_MESSAGE
        )
        kmeSdk.roomController.listen(
            conversationsHandler,
            KmeMessageEvent.RECEIVE_CONVERSATIONS,
            KmeMessageEvent.GOT_CONVERSATION
        )

        kmeSdk.roomController.chatModule.loadConversation(roomId, companyId)
    }

    fun openConversation(conversation: MappedConversation) {
        conversation.unreadCount = 0
        conversationChanged.value = conversation
        openConversation.value = conversation
        openedConversation = conversation
        conversationsList.updateItem(conversation)
        notifyUnreadMessagesCounter()
    }

    fun setOpenedConversation(conversation: MappedConversation?) {
        openedConversation = conversation
    }

    private val startPrivateChatHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            val msg: KmeChatModuleMessage<CreatedDmConversationPayload>? =
                message.toType()

            msg?.payload?.conversation?.let {
                val mappedConversation = it.mapConversation(timeUtil)
                openConversation(mappedConversation)
                handleNewConversation(mappedConversation)
            }
        }
    }

    private val messageHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            val receivedMessage: KmeChatModuleMessage<ReceiveMessagePayload>? =
                message.toType()

            receivedMessage?.payload?.conversationId?.let {
                validateConversation(it)
            }
        }
    }

    private val conversationsHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.RECEIVE_CONVERSATIONS -> {
                    val conversationMessage: KmeChatModuleMessage<ReceiveConversationsPayload>? =
                        message.toType()

                    val data =
                        conversationMessage?.payload?.conversations?.mapConversations(timeUtil)
                            ?: emptyList()
                    conversationsList.clear()
                    conversationsList.addAll(data)
                    conversationsList.addAll(privateChatsList)

                    conversations.postValue(conversationsList.filter())
                }
                KmeMessageEvent.GOT_CONVERSATION -> {
                    val msg: KmeChatModuleMessage<GotConversationPayload>? =
                        message.toType()
                    msg?.payload?.conversation?.let {
                        handleNewConversation(it.mapConversation(timeUtil))
                    }
                }
                else -> {
                }
            }
        }
    }

    fun filter() {
        val filtered = conversationsList.filter()
        conversations.value = filtered
        notifyUnreadMessagesCounter()
    }

    private fun List<MappedConversation>.filter(): List<MappedConversation> {
        val chatSettings =
            kmeSdk.userController.getCurrentParticipant()?.userPermissions?.chatModule

        forEach {
            it.hasAccess = it.isSystem == false ||
                    (it.conversationType == KmeConversationType.MODERATORS && kmeSdk.userController.getCurrentParticipant()
                        .isModerator()) ||
                    (it.isSystem == true && it.conversationType == KmeConversationType.QNA &&
                            chatSettings?.defaultSettings?.qnaChat == KmePermissionValue.ON) ||
                    (it.isSystem == true && it.conversationType == KmeConversationType.PUBLIC &&
                            chatSettings?.defaultSettings?.publicChat == KmePermissionValue.ON)

            if (it.hasAccess == false) {
                it.closeIfNoAccess()
            }
        }

        return this.filter {
            it.hasAccess == true
        }
    }

    fun onChatSettingsChanged(settings: Pair<KmePermissionKey?, KmePermissionValue?>) {
        val conversation: MappedConversation? = when (settings.first) {
            KmePermissionKey.QNA_CHAT -> {
                conversationsList.find { conversation -> conversation.conversationType == KmeConversationType.QNA }
            }
            KmePermissionKey.PUBLIC_CHAT -> {
                conversationsList.find { conversation -> conversation.conversationType == KmeConversationType.PUBLIC }
            }
            KmePermissionKey.START_PRIVATE_CHAT -> {
                conversationsList.find { conversation -> conversation.conversationType == KmeConversationType.PRIVATE }
            }
            else -> null
        }

        conversation?.let {
            val indexOf = conversationsList.indexOf(it)
            if (indexOf >= 0) {
                val hasAccess = KmePermissionValue.ON == settings.second
                it.hasAccess = hasAccess
                conversationsList[indexOf] = it
                conversations.value =
                    conversationsList.filter { kmeConversation -> kmeConversation.hasAccess == true }

                it.closeIfNoAccess()
            }
        }

    }

    private fun MappedConversation.closeIfNoAccess() {
        val isOpened = openedConversation == this
        if (hasAccess == false && isOpened) {
            closeConversation.value = this
        }
    }

    private fun validateConversation(conversationId: String) {
        val conversation =
            conversationsList.find { conversation -> conversation.id == conversationId }
        if (conversation == null) {
            kmeSdk.roomController.chatModule.getConversation(
                roomId,
                companyId,
                conversationId
            )
        }
    }

    private fun handleNewConversation(conversation: MappedConversation) {
        val foundConversation =
            conversationsList.find { localConversation -> localConversation.id == conversation.id }

        if (foundConversation == null) {
            conversation.increaseUnreadCount()
            conversationsList.add(conversation)
            privateChatsList.add(conversation)
            conversations.postValue(conversationsList.filter())
            notifyUnreadMessagesCounter()
        }
    }


    fun onNewMessage(message: MappedChatMessage?, increaseCount: Boolean) {
        val foundConversation =
            conversationsList.find { localConversation -> localConversation.id == message?.conversationId }

        if (foundConversation != null) {
            if (increaseCount) {
                foundConversation.increaseUnreadCount()
                notifyUnreadMessagesCounter()
            }
            foundConversation.lastMessage = message
            conversationChanged.value = foundConversation
            conversationsList.updateItem(foundConversation)
        }
    }

    private fun notifyUnreadMessagesCounter() {
        allUnreadMessageCounter.value = conversations.value?.getUnreadCounter() ?: 0
    }

    private fun MappedConversation.increaseUnreadCount() {
        this.unreadCount =
            if (this.id != openedConversation?.id) {
                this.unreadCount?.inc() ?: 1
            } else {
                0
            }
    }

    private fun List<MappedConversation>.getUnreadCounter(): Int {
        var unreadCounter = 0
        forEach {
            if (it.getUnreadCount() > 0) {
                unreadCounter += it.getUnreadCount()
            }
        }

        return unreadCounter
    }

    private fun MutableList<MappedConversation>.updateItem(item: MappedConversation) {
        val indexOf = this.indexOf(item)
        if (indexOf > 0) {
            this[indexOf] = item
        }
    }

    override fun onCleared() {
        super.onCleared()
        kmeSdk.roomController.removeListener(conversationsHandler)
        kmeSdk.roomController.removeListener(startPrivateChatHandler)
    }

}
