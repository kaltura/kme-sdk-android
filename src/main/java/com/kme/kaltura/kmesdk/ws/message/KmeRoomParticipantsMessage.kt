package com.kme.kaltura.kmesdk.ws.message

import com.google.gson.annotations.SerializedName

class KmeRoomParticipantsMessage<T : KmeRoomParticipantsMessage.RoomParticipantsPayload> : KmeMessage<T>() {

    data class MediaInitPayload(
        @SerializedName("company_id") val companyId: Long?,
        @SerializedName("live_media_state") val mediaState: String?,
        @SerializedName("mic_state") val micState: String?,
        @SerializedName("room_id") val roomId: Long?,
        @SerializedName("user_id") val userId: Long?,
        @SerializedName("webcam_state") val camState: String?
    ) : KmeRoomParticipantsMessage.RoomParticipantsPayload()

    open class RoomParticipantsPayload : Payload()

}
