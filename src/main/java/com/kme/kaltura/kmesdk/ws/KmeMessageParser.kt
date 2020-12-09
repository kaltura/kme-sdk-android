package com.kme.kaltura.kmesdk.ws

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.chat.KmeChatMessage
import com.kme.kaltura.kmesdk.ws.message.module.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.SetActiveContentPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeBannersModuleMessage.BannersPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeBannersModuleMessage.RoomPasswordStatusReceivedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeChatModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage.RoomDefaultSettingsChangedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage.RoomSettingsChangedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeVideoModuleMessage.*

private const val KEY_NAME = "name"

internal class KmeMessageParser(
    private val gson: Gson,
    private val jsonParser: JsonParser
) {

    fun parse(messageText: String): KmeMessage<KmeMessage.Payload>? {
        var parsedMessage: KmeMessage<KmeMessage.Payload>? = null
        try {
            val jsonObject = jsonParser.parse(messageText).asJsonObject
            if (jsonObject.has(KEY_NAME)) {
                val name = jsonObject.get(KEY_NAME).asString.toLowerCase()
                parsedMessage = parseMessage(name, messageText)
            }
        } catch (e: Exception) {
            parsedMessage = null
            e.printStackTrace()
        }

        //Unknown message type
        return parsedMessage
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseMessage(name: String, text: String): KmeMessage<KmeMessage.Payload>? {
        return when (name) {
            KmeMessageEvent.INSTRUCTOR_IS_OFFLINE.toString() -> {
                text.jsonToObject<KmeRoomInitModuleMessage<InstructorIsOfflinePayload>>()
            }
            KmeMessageEvent.ANY_INSTRUCTORS_IS_CONNECTED_TO_ROOM.toString() -> {
                text.jsonToObject<KmeRoomInitModuleMessage<AnyInstructorsIsConnectedToRoomPayload>>()
            }
            KmeMessageEvent.JOINED_ROOM.toString() -> {
                text.jsonToObject<KmeRoomInitModuleMessage<JoinedRoomPayload>>()
            }
            KmeMessageEvent.ROOM_STATE.toString() -> {
                text.jsonToObject<KmeRoomInitModuleMessage<RoomStatePayload>>()
            }
            KmeMessageEvent.ROOM_AVAILABLE_FOR_PARTICIPANT.toString() -> {
                text.jsonToObject<KmeRoomInitModuleMessage<RoomAvailableForParticipantPayload>>()
            }
            KmeMessageEvent.ROOM_PARTICIPANT_LIMIT_REACHED.toString() -> {
                text.jsonToObject<KmeRoomInitModuleMessage<RoomParticipantLimitReachedPayload>>()
            }
            KmeMessageEvent.AWAIT_INSTRUCTOR_APPROVAL.toString(),
            KmeMessageEvent.USER_REJECTED_BY_INSTRUCTOR.toString(),
            KmeMessageEvent.USER_APPROVED_BY_INSTRUCTOR.toString() -> {
                text.jsonToObject<KmeRoomInitModuleMessage<ApprovalPayload>>()
            }
            KmeMessageEvent.CLOSE_WEB_SOCKET.toString() -> {
                text.jsonToObject<KmeRoomInitModuleMessage<CloseWebSocketPayload>>()
            }
            KmeMessageEvent.NEW_USER_JOINED.toString() -> {
                text.jsonToObject<KmeRoomInitModuleMessage<NewUserJoinedPayload>>()
            }
            KmeMessageEvent.ROOM_HAS_PASSWORD.toString() -> {
                text.jsonToObject<KmeBannersModuleMessage<BannersPayload>>()
            }
            KmeMessageEvent.ROOM_PASSWORD_STATUS_RECEIVED.toString() -> {
                text.jsonToObject<KmeBannersModuleMessage<RoomPasswordStatusReceivedPayload>>()
            }
            KmeMessageEvent.USER_MEDIA_STATE_INIT.toString() -> {
                text.jsonToObject<KmeParticipantsModuleMessage<UserMediaStateInitPayload>>()
            }
            KmeMessageEvent.USER_MEDIA_STATE_CHANGED.toString() -> {
                text.jsonToObject<KmeParticipantsModuleMessage<UserMediaStateChangedPayload>>()
            }
            KmeMessageEvent.CHANGE_USER_FOCUS_EVENT.toString() -> {
                text.jsonToObject<KmeParticipantsModuleMessage<ChangeUserFocusEventPayload>>()
            }
            KmeMessageEvent.SET_PARTICIPANT_MODERATOR.toString() -> {
                text.jsonToObject<KmeParticipantsModuleMessage<SetParticipantModerator>>()
            }
            KmeMessageEvent.USER_HAND_RAISED.toString() -> {
                text.jsonToObject<KmeParticipantsModuleMessage<UserRaiseHandPayload>>()
            }
            KmeMessageEvent.MAKE_ALL_USERS_HAND_PUT.toString() -> {
                text.jsonToObject<KmeParticipantsModuleMessage<AllUsersHandPutPayload>>()
            }
            KmeMessageEvent.SDP_ANSWER_TO_PUBLISHER.toString() -> {
                text.jsonToObject<KmeStreamingModuleMessage<SdpAnswerToPublisherPayload>>()
            }
            KmeMessageEvent.USER_STARTED_TO_PUBLISH.toString() -> {
                text.jsonToObject<KmeStreamingModuleMessage<StartedPublishPayload>>()
            }
            KmeMessageEvent.SDP_OFFER_FOR_VIEWER.toString() -> {
                text.jsonToObject<KmeStreamingModuleMessage<SdpOfferToViewerPayload>>()
            }
            KmeMessageEvent.USER_DISCONNECTED.toString() -> {
                text.jsonToObject<KmeStreamingModuleMessage<UserDisconnectedPayload>>()
            }
            KmeMessageEvent.RECEIVE_CONVERSATIONS.toString() -> {
                text.jsonToObject<KmeChatModuleMessage<ReceiveConversationsPayload>>()
            }
            KmeMessageEvent.GOT_CONVERSATION.toString() -> {
                text.jsonToObject<KmeChatModuleMessage<GotConversationPayload>>()
            }
            KmeMessageEvent.CREATED_DM_CONVERSATION.toString() -> {
                text.jsonToObject<KmeChatModuleMessage<CreatedDmConversationPayload>>()
            }
            KmeMessageEvent.LOAD_MESSAGES.toString() -> {
                val messages = text.jsonToObject<KmeChatModuleMessage<LoadMessagesPayload>>()
                        as KmeChatModuleMessage<LoadMessagesPayload>?

                messages?.payload?.messages?.forEach { message ->
                    message.metadata?.let {
                        message.parsedMetadata =
                            gson.fromJson(it, KmeChatMessage.Metadata::class.java)
                    }
                }

                return messages as KmeMessage<KmeMessage.Payload>?
            }
            KmeMessageEvent.RECEIVE_MESSAGE.toString() -> {
                val message = text.jsonToObject<KmeChatModuleMessage<ReceiveMessagePayload>>()
                        as KmeChatModuleMessage<ReceiveMessagePayload>?

                message?.payload?.metadata?.let {
                    message.payload?.parsedMetadata =
                        gson.fromJson(it, KmeChatMessage.Metadata::class.java)
                }

                return message as KmeMessage<KmeMessage.Payload>?
            }
            KmeMessageEvent.DELETED_MESSAGE.toString() -> {
                text.jsonToObject<KmeChatModuleMessage<DeleteMessagePayload>>()
            }

            KmeMessageEvent.ROOM_DEFAULT_SETTINGS_CHANGED.toString() -> {
                text.jsonToObject<KmeRoomSettingsModuleMessage<RoomDefaultSettingsChangedPayload>>()
            }
            KmeMessageEvent.ROOM_SETTINGS_CHANGED.toString() -> {
                text.jsonToObject<KmeRoomSettingsModuleMessage<RoomSettingsChangedPayload>>()
            }
            KmeMessageEvent.INIT_ACTIVE_CONTENT.toString(),
            KmeMessageEvent.SET_ACTIVE_CONTENT.toString() -> {
                text.jsonToObject<KmeActiveContentModuleMessage<SetActiveContentPayload>>()
            }
            KmeMessageEvent.SYNC_PLAYER_STATE.toString() -> {
                text.jsonToObject<KmeVideoModuleMessage<SyncPlayerStatePayload>>()
            }
            KmeMessageEvent.PLAYER_PLAYING.toString() -> {
                text.jsonToObject<KmeVideoModuleMessage<VideoPayload>>()
            }
            KmeMessageEvent.PLAYER_PAUSED.toString() -> {
                text.jsonToObject<KmeVideoModuleMessage<VideoPayload>>()
            }
            KmeMessageEvent.PLAYER_SEEK_TO.toString() -> {
                text.jsonToObject<KmeVideoModuleMessage<VideoPayload>>()
            }
            else -> null
        }
    }

    private inline fun <reified T> String.jsonToObject(): KmeMessage<KmeMessage.Payload>? {
        return gson.fromJson(this, object : TypeToken<T>() {}.type)
    }

}
