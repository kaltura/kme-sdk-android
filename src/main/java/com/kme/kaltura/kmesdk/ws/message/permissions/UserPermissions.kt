package com.kme.kaltura.kmesdk.ws.message.permissions

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.permissions.*

data class UserPermissions(
    @SerializedName("notes_module") val notesModule: NotesModule,
    @SerializedName("screen_share_module") val screenShareModule: ScreenShareModule,
    @SerializedName("whiteboard_module") val whiteboardModule: WhiteboardModule,
    @SerializedName("participants_module") val participantsModule: ParticipantsModule,
    @SerializedName("chat_module") val chatModule: ChatModule,
    @SerializedName("analytics_module") val analyticsModule: AnalyticsModule,
    @SerializedName("files_module") val filesModule: FilesModule,
    @SerializedName("playlist_module") val playlistModule: PlaylistModule
)