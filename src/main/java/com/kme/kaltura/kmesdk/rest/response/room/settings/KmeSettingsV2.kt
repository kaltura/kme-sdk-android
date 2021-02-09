package com.kme.kaltura.kmesdk.rest.response.room.settings

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeSettingsV2(
    @SerializedName("company_settings")
    var companySettings: KmeCompanySettings? = null,

    @SerializedName("general")
    var general: KmeGeneral? = null,

    @SerializedName("chat_module")
    var chatModule: KmeChatModule? = null,

    @SerializedName("recording_module")
    var recordingModule: KmeRecordingModule? = null,

    @SerializedName("participants_module")
    var participantsModule: KmeParticipantsModule? = null,

    @SerializedName("notes_module")
    var notesModule: KmeNotesModule? = null,

    @SerializedName("playlist_module")
    var playlistModule: KmePlaylistModule? = null,

    @SerializedName("whiteboard_module")
    var whiteboardModule: KmeWhiteboardModule? = null,

    @SerializedName("screen_share_module")
    var screenShareModule: KmeScreenShareModule? = null,

    @SerializedName("youtube_module")
    var youtubeModule: KmeYoutubeModule? = null,

    @SerializedName("breakout_module")
    var breakoutModule: KmeBreakoutModule? = null,

    @SerializedName("quiz_module")
    var quizModule: KmeQuizModule? = null,

    @SerializedName("files_module")
    var filesModule: KmeFilesModule? = null,

    @SerializedName("close_captioning_module")
    var closeCaptioningModule: KmeCloseCaptioningModule? = null,

    @SerializedName("room_access_module")
    var roomAccessModule: KmeRoomAccessModule? = null
) : KmeResponseData(), Parcelable
