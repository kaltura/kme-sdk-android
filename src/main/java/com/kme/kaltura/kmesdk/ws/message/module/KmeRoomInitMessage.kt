package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageReason
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.permission.KmeUserPermissions
import com.kme.kaltura.kmesdk.ws.message.type.KmeClassMode

class KmeRoomInitMessage<T : KmeRoomInitMessage.RoomInitPayload> : KmeMessage<T>() {

    data class JoinRoomPayload(
        @SerializedName("pay") val pay: String?,
        @SerializedName("room_id") val roomId: Long?,
        @SerializedName("company_id") val companyId: Long?
    ) : RoomInitPayload()

    data class InstructorIsOfflinePayload(
        @SerializedName("room_id") val roomId: Long?
    ) : RoomInitPayload()

    data class AnyInstructorsIsConnectedToRoomPayload(
        @SerializedName("room_id") val roomId: Long?
    ) : RoomInitPayload()

    data class RoomAvailableForParticipantPayload(
        @SerializedName("room_id") val roomId: Long?
    ) : RoomInitPayload()

    data class RoomParticipantLimitReachedPayload(
        @SerializedName("room_id") val roomId: Long?,
        @SerializedName("user_id") val userId: Long?
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
        @SerializedName("user_permissions") val userPermissions: KmeUserPermissions,
        @SerializedName("is_moderator") val isModerator: Boolean,
        @SerializedName("is_captioner") val isCaptioner: Boolean,
        @SerializedName("lat") val lat: Double,
        @SerializedName("long") val long: Double
    ) : RoomInitPayload()

    data class RoomStatePayload(
        @SerializedName("metaData") val metaData: MetaData?,
        @SerializedName("participants") val participants: Map<String, KmeParticipant>?,
    ) : RoomInitPayload() {

        data class MetaData(
            @SerializedName("class_mode") val classMode: KmeClassMode? = null,
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

    data class NewUserJoinedPayload(
        @SerializedName("country") val country: String? = null,
        @SerializedName("user_permissions") val userPermissions: KmeUserPermissions? = null,
        @SerializedName("city") val city: String? = null,
        @SerializedName("last_unmute_time") val lastUnmuteTime: Double? = null,
        @SerializedName("device_type") val deviceType: String? = null,
        @SerializedName("connection_state") val connectionState: String? = null,
        @SerializedName("join_time") val joinTime: Double? = null,
        @SerializedName("long") val long: Double? = null,
        @SerializedName("user_role") val userRole: String? = null,
        @SerializedName("user_type") val userType: String? = null,
        @SerializedName("live_media_state") val liveMediaState: String? = null,
        @SerializedName("browser") val browser: String? = null,
        @SerializedName("region_name") val regionName: String? = null,
        @SerializedName("lat") val lat: Double? = null,
        @SerializedName("managing_server_id") val managingServerId: Long? = null,
        @SerializedName("out_of_tab_focus") val outOfTabFocus: Boolean? = null,
        @SerializedName("region_id") val regionId: Long? = null,
        @SerializedName("time_hand_raised") val timeHandRaised: Double? = null,
        @SerializedName("avatar") val avatar: String? = null,
        @SerializedName("is_moderator") val isModerator: Boolean? = null,
        @SerializedName("webcam_state") val webcamState: String? = null,
        @SerializedName("full_name") val fullName: String? = null,
        @SerializedName("is_captioner") val isCaptioner: Boolean? = null,
        @SerializedName("user_id") val userId: Long? = null,
        @SerializedName("mic_state") val micState: String? = null
    ) : RoomInitPayload()

    data class CloseWebSocketPayload(
        @SerializedName("room_id") val roomId: Long?,
        @SerializedName("is_instructor") val isInstructor: Boolean,
        @SerializedName("reason") val reason: KmeMessageReason?
    ) : RoomInitPayload()

    open class RoomInitPayload : Payload()

}