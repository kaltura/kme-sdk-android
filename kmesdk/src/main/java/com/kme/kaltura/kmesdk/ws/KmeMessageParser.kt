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
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeChatModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage.DesktopShareQualityUpdatedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage.DesktopShareStateUpdatedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeQuickPollModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomNotesMessage.CreateNotePayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomNotesMessage.NotePayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomRecordingMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage.RoomModuleSettingsChangedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage.RoomSettingsChangedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeSlidesPlayerModuleMessage.AnnotationStateChangedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeSlidesPlayerModuleMessage.SlideChangedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeVideoModuleMessage.SyncPlayerStatePayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeVideoModuleMessage.VideoPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.*

private const val KEY_NAME = "name"

/**
 * An implementation for parsing incoming messages
 */
internal class KmeMessageParser(
    private val gson: Gson,
    private val jsonParser: JsonParser
) {

    /**
     * Parse string message to [KmeMessage]
     *
     * @param messageText string message representation
     * @return [KmeMessage] object in case parsed correctly
     */
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

    /**
     * Parse string message to [KmeMessage]
     *
     * @param name name of event to parse
     * @param text string message representation
     * @return [KmeMessage] object in case parsed correctly
     */
    @Suppress("UNCHECKED_CAST")
    private fun parseMessage(name: String, text: String): KmeMessage<KmeMessage.Payload>? {
        return when (name) {
            KmeMessageEvent.COMBINED_EVENT.toString() -> {
                text.jsonToObject<KmeMessage<KmeMessage.Payload>>()
            }
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
            KmeMessageEvent.PARTICIPANT_MUTED.toString() -> {
                text.jsonToObject<KmeParticipantsModuleMessage<UserMediaStateChangedPayload>>()
            }
            KmeMessageEvent.ALL_PARTICIPANTS_MUTED.toString() -> {
                text.jsonToObject<KmeParticipantsModuleMessage<AllParticipantsMutedPayload>>()
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
            KmeMessageEvent.REMOVE_USER.toString() -> {
                text.jsonToObject<KmeParticipantsModuleMessage<RemoveParticipantPayload>>()
            }
            KmeMessageEvent.USER_REMOVED.toString() -> {
                text.jsonToObject<KmeParticipantsModuleMessage<ParticipantRemovedPayload>>()
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
            KmeMessageEvent.USER_SPEAKING.toString() -> {
                text.jsonToObject<KmeStreamingModuleMessage<UserSpeakingPayload>>()
            }
            KmeMessageEvent.RECEIVE_CONVERSATIONS.toString() -> {
                text.jsonToObject<KmeChatModuleMessage<ReceiveConversationsPayload>>()
            }
            KmeMessageEvent.GOT_CONVERSATION.toString() -> {
                text.jsonToObject<KmeChatModuleMessage<GotConversationPayload>>()
            }
            KmeMessageEvent.CLEARED_ALL_MESSAGES.toString() -> {
                text.jsonToObject<KmeChatModuleMessage<ClearedAllMessagesPayload>>()
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

            KmeMessageEvent.ROOM_NOTE_CREATED.toString() -> {
                text.jsonToObject<KmeRoomNotesMessage<CreateNotePayload>>()
            }
            KmeMessageEvent.ROOM_NOTE_RENAMED.toString() -> {
                text.jsonToObject<KmeRoomNotesMessage<NotePayload>>()
            }
            KmeMessageEvent.BROADCAST_ROOM_NOTE_TO_ALL.toString() -> {
                text.jsonToObject<KmeRoomNotesMessage<NotePayload>>()
            }
            KmeMessageEvent.ROOM_NOTE_SEND_TO_LISTENERS.toString() -> {
                text.jsonToObject<KmeRoomNotesMessage<NotePayload>>()
            }
            KmeMessageEvent.ROOM_NOTE_DELETED.toString() -> {
                text.jsonToObject<KmeRoomNotesMessage<NotePayload>>()
            }

            KmeMessageEvent.RECORDING_STARTED.toString() -> {
                text.jsonToObject<KmeRoomRecordingMessage<RecordingStartingPayload>>()
            }
            KmeMessageEvent.RECORDING_INITIATED.toString() -> {
                text.jsonToObject<KmeRoomRecordingMessage<RecordingInitiatedPayload>>()
            }
            KmeMessageEvent.RECORDING_STARTING.toString() -> {
                text.jsonToObject<KmeRoomRecordingMessage<RecordingStartedPayload>>()
            }
            KmeMessageEvent.RECORDING_STOPPED.toString() -> {
                text.jsonToObject<KmeRoomRecordingMessage<RecordingStoppedPayload>>()
            }
            KmeMessageEvent.RECORDING_COMPLETED.toString() -> {
                text.jsonToObject<KmeRoomRecordingMessage<RecordingCompletedPayload>>()
            }
            KmeMessageEvent.RECORDING_CONVERSION_COMPLETED.toString() -> {
                text.jsonToObject<KmeRoomRecordingMessage<RecordingConversionCompletedPayload>>()
            }
            KmeMessageEvent.RECORDING_UPLOAD_COMPLETED.toString() -> {
                text.jsonToObject<KmeRoomRecordingMessage<RecordingUploadCompletedPayload>>()
            }
            KmeMessageEvent.RECORDING_STATUS.toString() -> {
                text.jsonToObject<KmeRoomRecordingMessage<RecordingStatusPayload>>()
            }
            KmeMessageEvent.RECORDING_FAILED.toString() -> {
                text.jsonToObject<KmeRoomRecordingMessage<RecordingFailurePayload>>()
            }

            KmeMessageEvent.ROOM_MODULE_SETTINGS_CHANGED.toString() -> {
                text.jsonToObject<KmeRoomSettingsModuleMessage<RoomModuleSettingsChangedPayload>>()
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
            KmeMessageEvent.DESKTOP_SHARE_STATE_UPDATED.toString() -> {
                text.jsonToObject<KmeDesktopShareModuleMessage<DesktopShareStateUpdatedPayload>>()
            }
            KmeMessageEvent.DESKTOP_SHARE_QUALITY_UPDATED.toString() -> {
                text.jsonToObject<KmeDesktopShareModuleMessage<DesktopShareQualityUpdatedPayload>>()
            }
            KmeMessageEvent.SLIDE_CHANGED.toString() -> {
                text.jsonToObject<KmeSlidesPlayerModuleMessage<SlideChangedPayload>>()
            }
            KmeMessageEvent.ANNOTATIONS_STATE_CHANGED.toString() -> {
                text.jsonToObject<KmeSlidesPlayerModuleMessage<AnnotationStateChangedPayload>>()
            }
            KmeMessageEvent.WHITEBOARD_PAGE_DATA.toString() -> {
                text.jsonToObject<KmeWhiteboardModuleMessage<WhiteboardPageDataPayload>>()
            }
            KmeMessageEvent.RECEIVE_LASER_POSITION.toString() -> {
                text.jsonToObject<KmeWhiteboardModuleMessage<ReceivedLaserPositionPayload>>()
            }
            KmeMessageEvent.LASER_DEACTIVATED.toString() -> {
                text.jsonToObject<KmeWhiteboardModuleMessage<LaserDeactivatedPayload>>()
            }
            KmeMessageEvent.RECEIVE_DRAWING.toString(), KmeMessageEvent.RECEIVE_TRANSFORMATION.toString()-> {
                val message =  text.jsonToObject<KmeWhiteboardModuleMessage<ReceiveDrawingPayload>>()
                        as KmeWhiteboardModuleMessage<ReceiveDrawingPayload>?

                message?.payload?.drawing = WhiteboardPayload.Drawing().apply {
                    this.layer =  message?.payload?.drawingLayer
                    this.type = message?.payload?.drawingType
                    this.tool = message?.payload?.drawingTool
                    this.userId = message?.payload?.drawingUserId
                    this.userType = message?.payload?.drawingUserType
                    this.path = message?.payload?.drawingPath
                    this.createdDate = message?.payload?.drawingCreatedDate
                    this.fullUsername = message?.payload?.drawingFullUsername
                }

                return message as KmeMessage<KmeMessage.Payload>?
            }
            KmeMessageEvent.DELETE_DRAWING.toString() -> {
                text.jsonToObject<KmeWhiteboardModuleMessage<DeleteDrawingPayload>>()
            }
            KmeMessageEvent.WHITEBOARD_PAGE_CLEARED.toString() -> {
                text.jsonToObject<KmeWhiteboardModuleMessage<WhiteboardPageClearedPayload>>()
            }
            KmeMessageEvent.WHITEBOARD_ALL_PAGES_CLEARED.toString() -> {
                text.jsonToObject<KmeWhiteboardModuleMessage<WhiteboardPageClearedPayload>>()
            }
            KmeMessageEvent.WHITEBOARD_BACKGROUND_TYPE_CHANGED.toString() -> {
                text.jsonToObject<KmeWhiteboardModuleMessage<BackgroundTypeChangedPayload>>()
            }
            KmeMessageEvent.WHITEBOARD_SET_ACTIVE_PAGE.toString() -> {
                text.jsonToObject<KmeWhiteboardModuleMessage<SetActivePagePayload>>()
            }
            KmeMessageEvent.WHITEBOARD_PAGE_CREATED.toString() -> {
                text.jsonToObject<KmeWhiteboardModuleMessage<PageCreatedPayload>>()
            }
            KmeMessageEvent.QUICK_POLL_STARTED.toString() -> {
                text.jsonToObject<KmeQuickPollModuleMessage<QuickPollStartedPayload>>()
            }
            KmeMessageEvent.QUICK_POLL_ANSWERS.toString() -> {
                text.jsonToObject<KmeQuickPollModuleMessage<QuickPollAnswersStatePayload>>()
            }
            KmeMessageEvent.QUICK_POLL_ENDED.toString() -> {
                text.jsonToObject<KmeQuickPollModuleMessage<QuickPollEndedPayload>>()
            }
            KmeMessageEvent.QUICK_POLL_USER_ANSWERED.toString() -> {
                text.jsonToObject<KmeQuickPollModuleMessage<QuickPollUserAnsweredPayload>>()
            }
            KmeMessageEvent.MODULE_STATE.toString() -> {
                text.jsonToObject<KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>>()
            }
            KmeMessageEvent.BREAKOUT_START_SUCCESS.toString() -> {
                text.jsonToObject<KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>>()
            }
            KmeMessageEvent.BREAKOUT_STOP_SUCCESS.toString() -> {
                text.jsonToObject<KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>>()
            }
            KmeMessageEvent.BREAKOUT_ADD_ROOM_SUCCESS.toString() -> {
                text.jsonToObject<KmeBreakoutModuleMessage<BreakoutAddRoomPayload>>()
            }
            KmeMessageEvent.BREAKOUT_DELETE_ROOM_SUCCESS.toString() -> {
                text.jsonToObject<KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>>()
            }
            KmeMessageEvent.BREAKOUT_CHANGE_ROOM_NAME_SUCCESS.toString() -> {
                text.jsonToObject<KmeBreakoutModuleMessage<BreakoutChangeNamePayload>>()
            }
            KmeMessageEvent.BREAKOUT_ASSIGN_PARTICIPANTS_SUCCESS.toString() -> {
                text.jsonToObject<KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>>()
            }
            KmeMessageEvent.BREAKOUT_MOVE_TO_NEXT_ROOM.toString() -> {
                text.jsonToObject<KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>>()
            }
            KmeMessageEvent.BREAKOUT_RESHUFFLE_ASSIGNMENTS_SUCCESS.toString() -> {
                text.jsonToObject<KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>>()
            }
            KmeMessageEvent.BREAKOUT_CLEAR_ASSIGNMENTS_SUCCESS.toString() -> {
                text.jsonToObject<KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>>()
            }
            KmeMessageEvent.BREAKOUT_MODERATOR_JOINED_SUCCESS.toString() -> {
                text.jsonToObject<KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>>()
            }
            KmeMessageEvent.BREAKOUT_USER_JOINED_SUCCESS.toString() -> {
                text.jsonToObject<KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>>()
            }
            KmeMessageEvent.BREAKOUT_EXTEND_TIME_LIMIT_SUCCESS.toString() -> {
                text.jsonToObject<KmeBreakoutModuleMessage<BreakoutExtendTimePayload>>()
            }
            KmeMessageEvent.BREAKOUT_CALL_TO_INSTRUCTOR_SUCCESS.toString() -> {
                text.jsonToObject<KmeBreakoutModuleMessage<BreakoutRoomStatusPayload>>()
            }
            KmeMessageEvent.BREAKOUT_INSTRUCTOR_MESSAGE.toString() -> {
                text.jsonToObject<KmeBreakoutModuleMessage<BreakoutMessagePayload>>()
            }
            else -> null
        }
    }

    /**
     * Cast string to JsonObject using Gson library
     *
     * @param T class we need to cast to
     * @return [KmeMessage] object in case parsed correctly
     */
    private inline fun <reified T> String.jsonToObject(): KmeMessage<KmeMessage.Payload>? {
        return gson.fromJson(this, object : TypeToken<T>() {}.type)
    }

}
