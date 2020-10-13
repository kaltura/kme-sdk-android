package com.kme.kaltura.kmesdk.ws.message

import com.google.gson.annotations.SerializedName

data class UserPermissions(
    @SerializedName("notes_module") val notesModule: UserPermissionModule,
    @SerializedName("screen_share_module") val screenShareModule: UserPermissionModule,
    @SerializedName("whiteboard_module") val whiteboardModule: UserPermissionModule,
    @SerializedName("participants_module") val participantsModule: UserPermissionModule,
    @SerializedName("chat_module") val chatModule: UserPermissionModule,
    @SerializedName("analytics_module") val analyticsModule: UserPermissionModule,
    @SerializedName("files_module") val filesModule: UserPermissionModule,
    @SerializedName("playlist_module") val playlistModule: UserPermissionModule
)