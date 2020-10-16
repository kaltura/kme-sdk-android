package com.kme.kaltura.kmesdk.ws.message

import com.google.gson.annotations.SerializedName

class KmeStreamingMessage<T : KmeStreamingMessage.StreamingPayload> : KmeMessage<T>() {

    data class StartPublishingPayload(
        @SerializedName("company_id") val companyId: Long,
        @SerializedName("room_id") val roomId: Long,
        @SerializedName("sdpOffer") val sdpOffer: SDPOffer,
        @SerializedName("streamType") val streamType: String,
        @SerializedName("user_id") val userId: Long,
    ) : KmeStreamingMessage.StreamingPayload() {

        data class SDPOffer(
            @SerializedName("type") val type: String,
            @SerializedName("sdp") val sdp: String
        )

    }

    open class StreamingPayload : Payload()

}
