package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmePlayerState

class KmeVideoModuleMessage<T : KmeVideoModuleMessage.VideoPayload> : KmeMessage<T>() {

    data class SyncPlayerStatePayload(
        @SerializedName("player_state") val playerState: KmePlayerState? = null
    ) : VideoPayload()

    data class GetPlayerStatePayload(
        @SerializedName("room_id") val roomId: Long? = null,
        @SerializedName("company_id") val companyId: Long? = null,
        @SerializedName("explicitRequest") val explicitRequest: Boolean? = null
    ) : VideoPayload()

    open class VideoPayload : Payload() {
        @SerializedName("playerName")
        val playerName: String? = null
        @SerializedName("time")
        val time: String? = null
    }

}
