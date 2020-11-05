package com.kme.kaltura.kmesdk.rest.response.room.settings

import com.google.gson.annotations.SerializedName

data class KmeSettings(
    @SerializedName("is_secure_lti")
    val isSecureLti: Int? = null,

    @SerializedName("free_invites_count")
    val freeInvitesCount: Int? = null,

    @SerializedName("filter_deleted_room_from_files")
    val filterDeletedRoomFromFiles: Int? = null,

    @SerializedName("file_share_feature")
    val fileShareFeature: Int? = null,

    @SerializedName("order_playlist_files_by_name")
    val orderPlaylistFilesByName: Int? = null,

    @SerializedName("allow_only_admins_to_manage_files_playlist")
    val allowOnlyAdminsToManageFilesPlaylist: Int? = null,

    @SerializedName("is_webhook_enabled")
    val isWebhookEnabled: Int? = null,

    @SerializedName("youtube_search_limit_free")
    val youtubeSearchLimitFree: Int? = null,

    @SerializedName("youtube_search_limit_paid")
    val youtubeSearchLimitPaid: Int? = null,

    @SerializedName("instructors_can_access_recordings")
    val instructorsCanAccessRecordings: Int? = null,

    @SerializedName("youtube_private_token")
    val youtubePrivateToken: Any? = null,

    @SerializedName("default_room_version")
    val defaultRoomVersion: String? = null,

    @SerializedName("is_unlimited_account")
    val isUnlimitedAccount: Int? = null,

    @SerializedName("create_lti_token")
    val createLtiToken: Int? = null,

    @SerializedName("add_lti_request_data")
    val addLtiRequestData: Int? = null,

    @SerializedName("hide_student_profile")
    val hideStudentProfile: Int? = null,

    @SerializedName("show_nr2_wel_msg")
    val showNr2WelMsg: Int? = null,

    @SerializedName("rec_auto_start")
    val recAutoStart: Int? = null,

    @SerializedName("enable_virus_scan")
    val enableVirusScan: Int? = null,

    @SerializedName("rec_auto_selfpaced_upload")
    val recAutoSelfpacedUpload: Int? = null,

    @SerializedName("rec_set_reminder")
    val recSetReminder: Int? = null,

    @SerializedName("show_participant")
    val showParticipant: Int? = null,

    @SerializedName("show_invite")
    val showInvite: Int? = null,

    @SerializedName("show_chat")
    val showChat: Int? = null,

    @SerializedName("show_chat_moderator")
    val showChatModerator: Int? = null,

    @SerializedName("show_chat_questions")
    val showChatQuestions: Int? = null,

    @SerializedName("show_ondemand_page_link")
    val showOndemandPageLink: Int? = null,

    @SerializedName("enable_guests_to_join")
    val enableGuestsToJoin: Int? = null,

    @SerializedName("codec_type")
    val codecType: String? = null,

    @SerializedName("callstats_account")
    val callstatsAccount: String? = null,

    @SerializedName("default_participant_sort")
    val defaultParticipantSort: String? = null,

    @SerializedName("is_callstats_enabled")
    val isCallstatsEnabled: Int? = null,

    @SerializedName("is_callstats_account_upgraded")
    val isCallstatsAccountUpgraded: Int? = null,

    @SerializedName("show_session_stats_chat_history")
    val showSessionStatsChatHistory: Int? = null,

    @SerializedName("force_callstats")
    val forceCallstats: Int? = null,

    @SerializedName("auto_clear_chat_end_of_session")
    val autoClearChatEndOfSession: Int? = null,

    @SerializedName("show_kaltura_media")
    val showKalturaMedia: Int? = null,

    @SerializedName("show_language_selection")
    val showLanguageSelection: Int? = null,

    @SerializedName("push_to_unmute")
    val pushToUnmute: Int? = null,

    @SerializedName("k12_support")
    val k12Support: Int? = null,

    @SerializedName("push_to_unmute_with_auto_add")
    val pushToUnmuteWithAutoAdd: Int? = null,

    @SerializedName("show_speaking_indicator")
    val showSpeakingIndicator: Int? = null,

    @SerializedName("block_users_without_custom_metadata")
    val blockUsersWithoutCustomMetadata: Int? = null,

    @SerializedName("disable_video_upload")
    val disableVideoUpload: Int? = null,

    @SerializedName("nr1_alignment_in_nr2")
    val nr1AlignmentInNr2: Int? = null,

    @SerializedName("speaker_indicator_type")
    val speakerIndicatorType: String? = null,

    @SerializedName("hide_class_mode_settings")
    val hideClassModeSettings: Int? = null,

    @SerializedName("hide_end_session")
    val hideEndSession: Int? = null,

    @SerializedName("hide_leave_session")
    val hideLeaveSession: Int? = null

)