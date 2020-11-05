package com.kme.kaltura.kmesdk.ws.message.permission

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.room.settings.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeUserPermissions(
    @SerializedName("notes_module") var notesModule: KmeNotesModule? = null,
    @SerializedName("screen_share_module") var screenShareModule: KmeScreenShareModule? = null,
    @SerializedName("whiteboard_module") var whiteboardModule: KmeWhiteboardModule? = null,
    @SerializedName("participants_module") var participantsModule: KmeParticipantsModule? = null,
    @SerializedName("chat_module") var chatModule: KmeChatModule? = null,
    @SerializedName("analytics_module") var analyticsModule: KmeAnalyticsModule? = null,
    @SerializedName("files_module") var filesModule: KmeFilesModule? = null,
    @SerializedName("playlist_module") var playlistModule: KmePlaylistModule? = null
) : Parcelable