package com.kme.kaltura.kmesdk.rest.response.room.settings

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeDefaultSettings(
    @SerializedName("is_moderator")
    @Expose
    var isModerator: KmePermissionValue? = null,

    @SerializedName("qna_chat")
    @Expose
    var qnaChat: KmePermissionValue? = null,

    @SerializedName("start_private_chat")
    @Expose
    var startPrivateChat: KmePermissionValue? = null,

    @SerializedName("public_chat")
    @Expose
    var publicChat: KmePermissionValue? = null,

    @SerializedName("moderator_chat")
    @Expose
    var moderatorChat: KmePermissionValue? = null,

    @SerializedName("show_session_stats_chat_history")
    @Expose
    var showSessionStatsChatHistory: String? = null,

    @SerializedName("auto_clear_chat_end_of_session")
    @Expose
    var autoClearChatEndOfSession: String? = null,

    @SerializedName("terms")
    @Expose
    var terms: KmePermissionValue? = null,

) : Parcelable