package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage

class KmeBannersModuleMessage<T : KmeBannersModuleMessage.BannersPayload> : KmeMessage<T>() {

    data class SendRoomPasswordPayload(
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("password") var password: String? = null
    ) : BannersPayload()

    data class RoomPasswordStatusReceivedPayload(
        @SerializedName("status") val status: Boolean? = null,
        @SerializedName("tryCount") val tryCount: Int? = null,
    ) : BannersPayload()

    open class BannersPayload : Payload()
}