package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeChangeRoomSettingsResponse
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserInfoData
import com.kme.kaltura.kmesdk.ws.message.chat.KmeChatMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeLoadType
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

/**
 * An interface for actions related to chat
 */
interface IKmeChatModule {

    /**
     * Loads chat messages for specific conversation
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param conversationId id of a conversation
     * @param loadType any available load type from [KmeLoadType]
     * @param fromMessageId last loaded message id
     */
    fun loadMessages(
        roomId: Long,
        companyId: Long,
        conversationId: String,
        loadType: KmeLoadType = KmeLoadType.INITIAL_LOAD,
        fromMessageId: String? = null
    )

    /**
     * Sends message to specific conversation
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param conversationId id of a conversation
     * @param message description message
     * @param userInfo active user information
     */
    fun sendMessage(
        roomId: Long,
        companyId: Long,
        conversationId: String,
        message: String,
        userInfo: KmeUserInfoData?
    )

    /**
     * Sends reply message to specific conversation
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param conversationId id of a conversation
     * @param message description message
     * @param replyMessage message to be replied
     * @param userInfo active user information
     */
    fun replyMessage(
        roomId: Long,
        companyId: Long,
        conversationId: String,
        message: String,
        replyMessage: KmeChatMessage,
        userInfo: KmeUserInfoData?
    )

    /**
     * Deletes message from the conversation
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param conversationId id of a conversation
     * @param messageId specify id of a message to delete
     */
    fun deleteMessage(
        roomId: Long,
        companyId: Long,
        conversationId: String,
        messageId: String
    )

    /**
     * Starts a private chat with participant in case specific setting is enabled
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param targetUserId specify user id for private chat
     */
    fun startPrivateChat(
        roomId: Long,
        companyId: Long,
        targetUserId: Long
    )

    /**
     * Getting direct messages conversation
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param conversationId id of a conversation
     */
    fun getConversation(
        roomId: Long,
        companyId: Long,
        conversationId: String
    )

    /**
     * Load all available room conversations
     *
     * @param roomId id of a room
     * @param companyId id of a company
     */
    fun loadConversation(
        roomId: Long,
        companyId: Long
    )

    /**
     * Change visibility of public chat
     *
     * @param roomId id of a room
     * @param value value flag
     * @param success function to handle success result. Contains [KmeChangeRoomSettingsResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun changePublicChatVisibility(
        roomId: Long,
        value: KmePermissionValue,
        success: (response: KmeChangeRoomSettingsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

}
