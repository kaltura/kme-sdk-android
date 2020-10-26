package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage

class KmeStreamingModuleMessage<T : KmeStreamingModuleMessage.StreamingPayload> :
    KmeMessage<T>() {

    data class UserDisconnectedPayload(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null
    ) : StreamingPayload()

    open class StreamingPayload : Payload()
}