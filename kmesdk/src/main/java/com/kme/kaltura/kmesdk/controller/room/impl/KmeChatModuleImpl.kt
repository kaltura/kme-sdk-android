package com.kme.kaltura.kmesdk.controller.room.impl

import android.annotation.SuppressLint
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeChatModule
import com.kme.kaltura.kmesdk.controller.room.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeChangeRoomSettingsResponse
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserInfoData
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeChatApiService
import com.kme.kaltura.kmesdk.util.messages.*
import com.kme.kaltura.kmesdk.ws.message.chat.KmeChatMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeLoadType
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionModule
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * An implementation actions related to chat
 */
class KmeChatModuleImpl : KmeController(), IKmeChatModule {

    private val chatApiService: KmeChatApiService by inject()
    private val webSocketModule: IKmeWebSocketModule by inject()

    private val uiScope = CoroutineScope(Dispatchers.Main)

    /**
     * Loads chat messages for specific conversation
     */
    override fun loadMessages(
        roomId: Long,
        companyId: Long,
        conversationId: String,
        loadType: KmeLoadType,
        fromMessageId: String?
    ) {
        webSocketModule.send(
            buildLoadMessagesMessage(
                roomId,
                companyId,
                conversationId,
                loadType,
                fromMessageId
            )
        )
    }

    /**
     * Sends message to specific conversation
     */
    override fun sendMessage(
        roomId: Long,
        companyId: Long,
        conversationId: String,
        message: String,
        userInfo: KmeUserInfoData?
    ) {
        webSocketModule.send(
            buildSendMessage(
                roomId,
                companyId,
                conversationId,
                message,
                userInfo
            )
        )
    }

    /**
     * Sends reply message to specific conversation
     */
    override fun replyMessage(
        roomId: Long,
        companyId: Long,
        conversationId: String,
        message: String,
        replyMessage: KmeChatMessage,
        userInfo: KmeUserInfoData?
    ) {
        webSocketModule.send(
            buildReplyMessage(
                roomId,
                companyId,
                conversationId,
                message,
                replyMessage,
                userInfo
            )
        )
    }

    /**
     * Deletes message from the conversation
     */
    override fun deleteMessage(
        roomId: Long,
        companyId: Long,
        conversationId: String,
        messageId: String
    ) {
        webSocketModule.send(buildDeleteMessage(roomId, companyId, conversationId, messageId))
    }

    /**
     * Starts a private chat with participant in case specific setting is enabled
     */
    override fun startPrivateChat(roomId: Long, companyId: Long, targetUserId: Long) {
        webSocketModule.send(buildStartPrivateChatMessage(roomId, companyId, targetUserId))
    }

    /**
     * Getting direct messages conversation
     */
    override fun getConversation(
        roomId: Long,
        companyId: Long,
        conversationId: String
    ) {
        webSocketModule.send(
            buildGetConversationMessage(
                roomId,
                companyId,
                conversationId
            )
        )
    }

    /**
     * Load all available room conversations
     */
    override fun loadConversation(roomId: Long, companyId: Long) {
        webSocketModule.send(buildLoadConversationsMessage(roomId, companyId))
    }

    /**
     * Change visibility of public chat
     */
    @SuppressLint("DefaultLocale")
    override fun changePublicChatVisibility(
        roomId: Long,
        value: KmePermissionValue,
        success: (response: KmeChangeRoomSettingsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                {
                    chatApiService.changePublicChatVisibility(
                        roomId,
                        KmePermissionModule.CHAT_MODULE.name.toLowerCase(),
                        KmePermissionKey.PUBLIC_CHAT.name.toLowerCase(),
                        value.name.toLowerCase()
                    )
                },
                success,
                error
            )
        }
    }

}
