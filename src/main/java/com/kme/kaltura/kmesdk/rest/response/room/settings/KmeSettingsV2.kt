package com.kme.kaltura.kmesdk.rest.response.room.settings

import com.google.gson.annotations.SerializedName

data class KmeSettingsV2(
    @SerializedName("company_settings")
    val companySettings: KmeCompanySettings? = null,

    @SerializedName("general")
    val general: KmeGeneral? = null,

    @SerializedName("chat_module")
    val chatModule: KmeChatModule? = null,

    @SerializedName("recording_module")
    val recordingModule: KmeRecordingModule? = null,

    @SerializedName("participants_module")
    val participantsModule: KmeParticipantsModule? = null,

    @SerializedName("notes_module")
    val notesModule: KmeNotesModule? = null,

    @SerializedName("playlist_module")
    val playlistModule: KmePlaylistModule? = null,

    @SerializedName("whiteboard_module")
    val whiteboardModule: KmeWhiteboardModule? = null,

    @SerializedName("screen_share_module")
    val screenShareModule: KmeScreenShareModule? = null,

    @SerializedName("youtube_module")
    val youtubeModule: KmeYoutubeModule? = null,

    @SerializedName("breakout_module")
    val breakoutModule: KmeBreakoutModule? = null,

    @SerializedName("quiz_module")
    val quizModule: KmeQuizModule? = null,

    @SerializedName("files_module")
    val filesModule: KmeFilesModule? = null,

    @SerializedName("close_captioning_module")
    val closeCaptioningModule: KmeCloseCaptioningModule? = null,

    @SerializedName("room_access_module")
    val roomAccessModule: KmeRoomAccessModule? = null
)