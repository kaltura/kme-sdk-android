package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaStateType

class KmeParticipantsModuleMessage<T : KmeParticipantsModuleMessage.ParticipantsPayload> :
    KmeMessage<T>() {

    data class UserMediaStateInitPayload(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("live_media_state") var liveMediaState: KmeMediaDeviceState? = null,
        @SerializedName("mic_state") var micState: KmeMediaDeviceState? = null,
        @SerializedName("webcam_state") var webcamState: KmeMediaDeviceState? = null
    ) : ParticipantsPayload()

    data class UserMediaStateChangedPayload(
        @SerializedName("user_id") var userId: Long,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("media_state_type") var mediaStateType: KmeMediaStateType? = null,
        @SerializedName("state_value") var stateValue: KmeMediaDeviceState? = null,
    ) : ParticipantsPayload()

    data class AllParticipantsMutedPayload(
        @SerializedName("user_id") var userId: Long,
        @SerializedName("media_type") var mediaStateType: KmeMediaStateType,
        @SerializedName("state_value") var stateValue: KmeMediaDeviceState
    ) : ParticipantsPayload()

    data class ChangeUserFocusEventPayload(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("is_focused") var isFocused: Boolean? = null
    ) : ParticipantsPayload()

    data class SetParticipantModerator(
        @SerializedName("target_user_id") var targetUserId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("is_moderator") var isModerator: Boolean? = null
    ) : ParticipantsPayload()

    data class UserRaiseHandPayload(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("isRaise") var isRaise: Boolean? = null,
        @SerializedName("target_user_id") var targetUserId: Long? = null,
    ) : ParticipantsPayload()

    data class AllUsersHandPutPayload(
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null
    ) : ParticipantsPayload()

    data class RemoveParticipantPayload(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("target_user_id") var targetUserId: Long? = null
    ) : ParticipantsPayload()

    data class ParticipantRemovedPayload(
        @SerializedName("target_user_id") var targetUserId: Long? = null
    ) : ParticipantsPayload()

    open class ParticipantsPayload : Payload()
}