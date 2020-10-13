package com.kme.kaltura.kmesdk.ws.message

import com.google.gson.annotations.SerializedName

class KmeRoomInitMessage<T : KmeRoomInitMessage.RoomInitPayload> : KmeMessage<T>() {

    data class JoinRoomPayload(
        @SerializedName("pay")
        val pay: String?,
        @SerializedName("room_id")
        val roomId: Long?,
        @SerializedName("company_id")
        val companyId: Long?
    ) : RoomInitPayload()

    data class InstructorIsOfflinePayload(
        @SerializedName("room_id")
        val roomId: Long?
    ) : RoomInitPayload()

    data class JoinedRoomPayload(
        @SerializedName("user_id") val userId: Long?,
        @SerializedName("region_id") val regionId: Long,
        @SerializedName("user_type") val userType: String,
        @SerializedName("avatar") val avatar: String,
        @SerializedName("user_role") val userRole: String,
        @SerializedName("full_name") val fullName: String,
        @SerializedName("region_name") val regionName: String,
        @SerializedName("join_time") val joinTime: Long,
        @SerializedName("connection_state") val connectionState: String,
        @SerializedName("live_media_state") val liveMediaState: String,
        @SerializedName("webcam_state") val webCamState: String,
        @SerializedName("mic_state") val micState: String,
        @SerializedName("time_hand_raised") val timeHandRaised: Long,
        @SerializedName("last_unmute_time") val lastUnmuteTime: Long,
        @SerializedName("device_type") val deviceType: String,
        @SerializedName("browser") val browser: String,
        @SerializedName("country") val country: String,
        @SerializedName("city") val city: String,
        @SerializedName("managing_server_id") val managingServerId: Long,
        @SerializedName("out_of_tab_focus") val outOfTabFocus: Boolean,
        @SerializedName("user_permissions") val userPermissions: UserPermissions,
        @SerializedName("is_moderator") val isModerator: Boolean,
        @SerializedName("is_captioner") val isCaptioner: Boolean,
        @SerializedName("lat") val lat: Double,
        @SerializedName("long") val long: Double
    ) : RoomInitPayload()

    data class RoomStatePayload(
        @SerializedName("metaData") val metaData: MetaData?
    ) : RoomInitPayload() {

        data class MetaData(
            @SerializedName("class_mode") val classMode: String? = null,
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
            @SerializedName("participants_order") val participantsOrder: String? = null
        )

    }

    data class CloseWebSocketPayload(
        @SerializedName("room_id") val roomId: Long?,
        @SerializedName("is_instructor") val isInstructor: Boolean,
        @SerializedName("reason") val reason: String?
    ) : RoomInitPayload()

    open class RoomInitPayload : Payload()

}