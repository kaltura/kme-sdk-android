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

    data class TermsAgreementPayload(
        @SerializedName("agreed") val agreed: Boolean? = null,
    ) : BannersPayload()

    data class TermsAgreedPayload(
        @SerializedName("room_id") val roomId: Long? = null,
        @SerializedName("user_id") val userId: Long? = null,
        ) : BannersPayload()

    data class TermsRejectedPayload(
        @SerializedName("room_id") val roomId: Long? = null,
        @SerializedName("user_id") val userId: Long? = null,
    ) : BannersPayload()

    open class BannersPayload : Payload()

}
