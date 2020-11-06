package com.kme.kaltura.kmesdk.ws.message.type.permissions

import com.google.gson.annotations.SerializedName

enum class KmePermissionModule(value: String) {

    @SerializedName("close_captioning_module", alternate = ["CLOSE_CAPTIONING_MODULE"])
    CLOSE_CAPTIONING_MODULE("close_captioning_module"),

    @SerializedName("chat_module", alternate = ["CHAT_MODULE"])
    CHAT_MODULE("chat_module"),

    @SerializedName("recording_module", alternate = ["RECORDING_MODULE"])
    RECORDING_MODULE("recording_module"),

    @SerializedName("participants_module", alternate = ["PARTICIPANTS_MODULE"])
    PARTICIPANTS_MODULE("participants_module"),

    @SerializedName("files_module", alternate = ["FILES_MODULE"])
    FILES_MODULE("files_module"),

    @SerializedName("playlist_module", alternate = ["PLAYLIST_MODULE"])
    PLAYLIST_MODULE("playlist_module"),

    @SerializedName("whiteboard_module", alternate = ["WHITEBOARD_MODULE"])
    WHITEBOARD_MODULE("whiteboard_module"),

    @SerializedName("notes_module", alternate = ["NOTES_MODULE"])
    NOTES_MODULE("notes_module"),

    @SerializedName("invite_module", alternate = ["INVITE_MODULE"])
    INVITE_MODULE("invite_module"),

    @SerializedName("breakout_module", alternate = ["BREAKOUT_MODULE"])
    BREAKOUT_MODULE("breakout_module"),

    @SerializedName("quiz_module", alternate = ["QUIZ_MODULE"])
    QUIZ_MODULE("quiz_module"),

    @SerializedName("youtube_module", alternate = ["YOUTUBE_MODULE"])
    YOUTUBE_MODULE("youtube_module")

}