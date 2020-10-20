package com.kme.kaltura.kmesdk.ws.message.permission

import com.google.gson.annotations.SerializedName

data class KmeUserPermissions(
    @SerializedName("notes_module") val notesModule: KmeNotesModule,
    @SerializedName("screen_share_module") val screenShareModule: KmeScreenShareModule,
    @SerializedName("whiteboard_module") val whiteboardModule: KmeWhiteboardModule,
    @SerializedName("participants_module") val participantsModule: KmeParticipantsModule,
    @SerializedName("chat_module") val chatModule: KmeChatModule,
    @SerializedName("analytics_module") val analyticsModule: KmeAnalyticsModule,
    @SerializedName("files_module") val filesModule: KmeFilesModule,
    @SerializedName("playlist_module") val playlistModule: KmePlaylistModule
)