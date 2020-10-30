package com.kme.kaltura.kmesdk.ws.message.chat

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.type.KmeConversationType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeConversation(

    @SerializedName("id") val id: String? = null,
    @SerializedName("lastMessage") val lastMessage: KmeChatMessage? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("conversation_type") val conversationType: KmeConversationType? = null,
    @SerializedName("is_system") val isSystem: Boolean? = null,
    @SerializedName("unreadMessages") val unreadMessages: Int? = null

) : Parcelable