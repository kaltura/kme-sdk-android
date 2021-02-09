package com.kme.kaltura.kmesdk.ws.message.chat

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.type.KmeConversationType
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeConversation(
    @SerializedName("id") val id: String? = null,
    @SerializedName("lastMessage") val lastMessage: KmeChatMessage? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("conversation_type") val conversationType: KmeConversationType? = null,
    @SerializedName("is_system") val isSystem: Boolean? = null,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("unreadMessages") val unreadMessages: Int? = null,
    @SerializedName("conversationUser1") val firstUser: User? = null,
    @SerializedName("conversationUser2") val secondUser: User? = null
) : Parcelable {
    var hasAccess = false

    @Parcelize
    data class User(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("user_name") var name: String? = null,
        @SerializedName("user_avatar") var avatar: String? = null,
        @SerializedName("user_type") var userType: KmeUserType? = null
    ) : Parcelable

}