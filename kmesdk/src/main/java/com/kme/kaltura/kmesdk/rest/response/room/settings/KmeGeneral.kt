package com.kme.kaltura.kmesdk.rest.response.room.settings

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeGeneral(
    @SerializedName("show_invite")
    val showInvite: String? = null,

    @SerializedName("enable_virus_scan")
    val enableVirusScan: String? = null,

    @SerializedName("enable_guests_to_join")
    val enableGuestsToJoin: String? = null,

    @SerializedName("show_ondemand_page_link")
    val showOndemandPageLink: String? = null,

    @SerializedName("show_language_selection")
    val showLanguageSelection: String? = null,

    @SerializedName("codec_type")
    val codecType: String? = null,

    @SerializedName("push_to_unmute")
    val pushToUnmute: String? = null,

    @SerializedName("push_to_unmute_with_auto_add")
    val pushToUnmuteWithAutoAdd: String? = null,

    @SerializedName("show_speaking_indicator")
    val showSpeakingIndicator: String? = null,

    @SerializedName("speaker_indicator_type")
    val speakerIndicatorType: String? = null,

    @SerializedName("k12_support")
    val k12Support: String? = null,

    @SerializedName("is_callstats_enabled")
    val isCallstatsEnabled: String? = null,

    @SerializedName("is_callstats_account_upgraded")
    val isCallstatsAccountUpgraded: String? = null,

    @SerializedName("callstats_account")
    val callstatsAccount: String? = null,

    @SerializedName("force_callstats")
    val forceCallstats: String? = null,

    @SerializedName("block_users_without_custom_metadata")
    val blockUsersWithoutCustomMetadata: String? = null,

    @SerializedName("show_kaltura_media")
    val showKalturaMedia: String? = null,

    @SerializedName("class_mode")
    var classMode: KmePermissionValue? = null,

    @SerializedName("mute_all_mics")
    val muteAllMics: KmePermissionValue? = null,

    @SerializedName("mute_all_cams")
    val muteAllCams: KmePermissionValue? = null,

    @SerializedName("use_ice_lite")
    val useIceLite: String? = null,

    @SerializedName("send_auto_react_crash_support_tickets")
    val sendAutoReactCrashSupportTickets: String? = null,

    @SerializedName("allow_auto_disconnection_fixes")
    val allowAutoDisconnectionFixes: String? = null,

    @SerializedName("hide_class_mode_settings")
    val hideClassModeSettings: String? = null,

    @SerializedName("hide_end_session")
    val hideEndSession: String? = null,

    @SerializedName("hide_leave_session")
    val hideLeaveSession: String? = null,

    @SerializedName("nr1_alignment_in_nr2")
    val nr1AlignmentInNr2: String? = null,

    @SerializedName("nr2_watchdog_auto_support")
    val nr2WatchdogAutoSupport: String? = null,

    @SerializedName("nr2_watchdog_auto_reconnect")
    val nr2WatchdogAutoReconnect: String? = null,

    @SerializedName("browser_focus_video_overlay")
    val browserFocusVideoOverlay: String? = null,

    @SerializedName("browser_focus_participant_list")
    val browserFocusParticipantList: String? = null,

    @SerializedName("streaming_settings")
    val streamingSettings: KmeStreamingSettings? = null,

    @SerializedName("session_expires_without_instructors")
    val sessionExpiresWithoutInstructors: String? = null,

    @SerializedName("session_expiry_timeout")
    val sessionExpiryTimeout: String? = null,

    @SerializedName("max_live_participants")
    val maxLiveParticipants: String? = null,

    @SerializedName("legacy_mute_buttons")
    val legacyMuteButtons: String? = null,

    @SerializedName("enable_mobile_video")
    val enableMobileVideo: String? = null,

    @SerializedName("user_timeout")
    val userTimeout: String? = null
) : Parcelable