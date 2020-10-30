package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeLiveMediaState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaStateType

class KmeParticipantsModuleMessage<T : KmeParticipantsModuleMessage.ParticipantsPayload> :
    KmeMessage<T>() {

    data class UserMediaStateInitPayload(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("live_media_state") var liveMediaState: KmeLiveMediaState? = null,
        @SerializedName("mic_state") var micState: KmeMediaDeviceState? = null,
        @SerializedName("webcam_state") var webcamState: KmeMediaDeviceState? = null
    ) : ParticipantsPayload()

    data class UserMediaStateChangedPayload(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("media_state_type") var mediaStateType: KmeMediaStateType? = null,
        @SerializedName("state_value") var stateValue: String? = null,
    ) : ParticipantsPayload()

    data class ChangeUserFocusEventPayload(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("is_focused") var isFocused: Boolean? = null
    ) : ParticipantsPayload()

    open class ParticipantsPayload : Payload()
}