package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage

class KmeStreamingMessage<T : KmeStreamingMessage.StreamingPayload> :
    KmeMessage<T>() {

    data class StartPublishingPayload(
        @SerializedName("company_id") val companyId: Long? = null,
        @SerializedName("room_id") val roomId: Long? = null,
        @SerializedName("sdpOffer") val sdpOffer: SDPOffer? = null,
        @SerializedName("streamType") val streamType: String? = null,
        @SerializedName("user_id") val userId: Long? = null
    ) : StreamingPayload() {

        data class SDPOffer(
            @SerializedName("type") val type: String? = null,
            @SerializedName("sdp") val sdp: String? = null
        )

    }

    data class SdpAnswerToPublisherPayload(
        @SerializedName("mediaServerId") var mediaServerId: Long? = null,
        @SerializedName("mediaServerIP") var mediaServerIP: String? = null,
        @SerializedName("plugin") var plugin: String? = null,
        @SerializedName("sdpAnswer") var sdpAnswer: String? = null,
        @SerializedName("user_id") var userId: String? = null
    ) : StreamingPayload()

    data class IceGatheringDonePayload(
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("mediaServerId") var mediaServerId: Long? = null,
        @SerializedName("plugin") var plugin: String? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("streamType") var streamType: String? = null,
        @SerializedName("userId") var userId: Long? = null
    ) : StreamingPayload()

    data class UserDisconnectedPayload(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null
    ) : StreamingPayload()

    open class StreamingPayload : Payload()
}
