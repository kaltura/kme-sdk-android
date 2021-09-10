package com.kme.kaltura.kmesdk.ws.message.room

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

data class KmeRoomMetaData(
    @SerializedName("class_mode") val classMode: KmePermissionValue? = null,
    @SerializedName("room_password") val roomPassword: String? = null,
    @SerializedName("room_id") val roomId: Long? = null,
    @SerializedName("company_id") val companyId: Long? = null,
    @SerializedName("presenter_id") val presenterId: Long? = null,
    @SerializedName("alias") val alias: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("max_users") val maxUsers: Int? = null,
    @SerializedName("max_guests") val maxGuests: Int? = null,
    @SerializedName("annotations_enabled") val annotationsEnabled: Boolean? = null,
    @SerializedName("auto_clear_chat_end_of_session") val autoClearChatEndOfSession: Boolean? = null,
    @SerializedName("managingRoomServersColons") val managingRoomServersColons: String? = null,
    @SerializedName("roomRedisKey") val roomRedisKey: String? = null,
    @SerializedName("participantsTableRedisKey") val participantsTableRedisKey: String? = null,
    @SerializedName("roomSystemConversationsKey") val roomSystemConversationsKey: String? = null,
    @SerializedName("roomActiveContentKey") val roomActiveContentKey: String? = null,
    @SerializedName("roomWhiteboardKey") val roomWhiteboardKey: String? = null,
    @SerializedName("roomDesktopShareKey") val roomDesktopShareKey: String? = null,
    @SerializedName("roomRecordingRedisKey") val roomRecordingRedisKey: String? = null,
    @SerializedName("roomCaptionRedisKey") val roomCaptionRedisKey: String? = null,
    @SerializedName("streamsTableRedisKey") val streamsTableRedisKey: String? = null,
    @SerializedName("sessionTimeoutTime") val sessionTimeoutTime: Long? = null,
    @SerializedName("roomSettingsRedisKey") val roomSettingsRedisKey: String? = null,
    @SerializedName("participants_order") val participantsOrder: String? = null,
)