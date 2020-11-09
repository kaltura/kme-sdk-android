package com.kme.kaltura.kmesdk.ws.message.chat

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserInfoData
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeChatMessage(

    @SerializedName("id") var id: String? = null,
    @SerializedName("conversation_id") val conversationId: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("message_metadata") val metadata: String? = null,
    @SerializedName("timestamp") val timestamp: Long? = null,
    @SerializedName("user") var user: KmeUserInfoData? = null,
    @SerializedName("reply_all") var replyAll: Boolean? = null,
    var parsedMetadata: Metadata? = null
) : Parcelable {

    @Parcelize
    data class Metadata(
        @SerializedName("id") var id: String? = null,
        @SerializedName("conversation_id") val conversationId: String? = null,
        @SerializedName("message") var message: String? = null,
        @SerializedName("timestamp") val timestamp: Long? = null,
        @SerializedName("user") var user: KmeUserInfoData? = null
    ): Parcelable

}
