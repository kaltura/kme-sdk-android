package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeLiveMediaState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaStateType

class KmeStreamingMessage<T : KmeStreamingMessage.StreamingPayload> :
    KmeMessage<T>() {

    data class UserDisconnectedPayload(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null
    ) : StreamingPayload()

    open class StreamingPayload : Payload()
}