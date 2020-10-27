package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage

class KmeStreamingMessage<T : KmeStreamingMessage.StreamingPayload> :
    KmeMessage<T>() {

    data class StartPublishingPayload(
        @SerializedName("room_id") val roomId: Long? = null,
        @SerializedName("user_id") val userId: Long? = null,
        @SerializedName("company_id") val companyId: Long? = null,
        @SerializedName("sdpOffer") val sdpOffer: SDP? = null,
        @SerializedName("streamType") val streamType: String? = null
    ) : StreamingPayload()

    data class StartedPublishPayload(
        @SerializedName("room_id") val sdpOffer: Long? = null,
        @SerializedName("user_id") val userId: String? = null,
        @SerializedName("managingRoomServerId") val managingRoomServerId: Long? = null
    ) : StreamingPayload()

    data class StartViewingPayload(
        @SerializedName("room_id") val sdpOffer: Long? = null,
        @SerializedName("user_id") val userId: Long? = null,
        @SerializedName("company_id") val companyId: Long? = null,
        @SerializedName("requestedUserIdStream") val requestedUserIdStream: Long? = null,
        @SerializedName("streamType") val streamType: String? = null
    ) : StreamingPayload()

    data class SdpAnswerToPublisherPayload(
        @SerializedName("mediaServerId") var mediaServerId: Long? = null,
        @SerializedName("mediaServerIP") var mediaServerIP: String? = null,
        @SerializedName("plugin") var plugin: String? = null,
        @SerializedName("sdpAnswer") var sdpAnswer: String? = null,
        @SerializedName("user_id") var userId: String? = null,
        @SerializedName("requestedUserIdStream") var requestedUserIdStream: String? = null
    ) : StreamingPayload()

    data class SdpAnswerToFromViewer(
        @SerializedName("room_id") val sdpOffer: Long? = null,
        @SerializedName("user_id") val userId: Long? = null,
        @SerializedName("company_id") val companyId: Long? = null,
        @SerializedName("plugin") var plugin: String? = null,
        @SerializedName("sdpAnswer") var sdpAnswer: SDP? = null,
        @SerializedName("requestedUserIdStream") var requestedUserIdStream: Long? = null,
        @SerializedName("mediaServerId") var mediaServerId: Long? = null,
        @SerializedName("streamType") var streamType: String? = null
    ) : StreamingPayload()

    data class SdpOfferToViewerPayload(
        @SerializedName("mediaServerId") var mediaServerId: Long? = null,
        @SerializedName("mediaServerIP") var mediaServerIP: String? = null,
        @SerializedName("plugin") var plugin: String? = null,
        @SerializedName("sdpOffer") var sdpOffer: String? = null,
        @SerializedName("requestedUserIdStream") var requestedUserIdStream: Long? = null,
        @SerializedName("user_id") var userId: Long? = null
    ) : StreamingPayload()

    data class IceGatheringPublishDonePayload(
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("userId") var userId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("mediaServerId") var mediaServerId: Long? = null,
        @SerializedName("plugin") var plugin: String? = null,
        @SerializedName("streamType") var streamType: String? = null
    ) : StreamingPayload()

    data class IceGatheringViewingDonePayload(
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("userId") var userId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("mediaServerId") var mediaServerId: Long? = null,
        @SerializedName("plugin") var plugin: String? = null,
        @SerializedName("requestedUserIdStream") var requestedUserIdStream: Long? = null,
        @SerializedName("streamType") var streamType: String? = null
    ) : StreamingPayload()

    data class UserDisconnectedPayload(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null
    ) : StreamingPayload()

    open class StreamingPayload : Payload() {
        data class SDP(
            @SerializedName("type") val type: String? = null,
            @SerializedName("sdp") val sdp: String? = null
        )
    }

}
