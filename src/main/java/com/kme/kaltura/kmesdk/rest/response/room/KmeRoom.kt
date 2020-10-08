package com.kme.kaltura.kmesdk.rest.response.room

import com.google.gson.annotations.SerializedName

data class KmeRoom(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String?,
    @SerializedName("is_ondemand") val isOnDemand: Boolean?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("alias") val alias: String?,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("company_avatar") val companyAvatar: String?,
    @SerializedName("company_id") val companyId: Long?,
    @SerializedName("company_name") val companyName: String?,
    @SerializedName("app_version") val appVersion: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("room_user_role") val roomUserRole: String?,
    @SerializedName("user_role") val userRole: String?,
    @SerializedName("is_user_moderator") val isUserModerator: String?,
    @SerializedName("instructors") val instructors: Instructor?,
    @SerializedName("type") val type: String?,
    @SerializedName("auto_upload_recordings") val autoUploadRecordings: Boolean?,
    @SerializedName("is_turn_configured") val isTurnConfigured: Boolean?,
    @SerializedName("privacy_level") val privacyLevel: String?,
    @SerializedName("allow_guest_enter_selfpaced") val allowGuestEnterSelfPaced: Boolean?,
    @SerializedName("allow_guest_enter_liveclass") val allowGuestEnterLiveClass: Boolean?,
    @SerializedName("show_join_liveclass") val showJoinLiveClass: Boolean?,
    @SerializedName("active_breakout_id") val activeBreakoutId: String?,
    @SerializedName("file_hierarchy_id") val fileHierarchyId: String?,
    @SerializedName("parent_room_id") val parentRoomId: String?,
    @SerializedName("index_id") val indexId: String?,
    @SerializedName("playlist_id") val playlistId: String?,
    @SerializedName("default_playlist_id") val defaultPlaylistId: String?,
    @SerializedName("migrate_log") val migrateLog: String?,
    @SerializedName("playlist_migrate_status") val playlistMigrateStatus: String?,
    @SerializedName("whiteboard_migrate_status") val whiteboardMigrateStatus: String?,
    @SerializedName("chat_migrate_status") val chatMigrateStatus: String?,
//    @SerializedName("settings") val settings: String?,
//    @SerializedName("settings_v2") val settingsV2: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("license_id") val licenseId: Int?,
    @SerializedName("max_guests") val maxGuests: Int?,
    @SerializedName("duration") val duration: Int?,
    @SerializedName("server_id") val serverId: String?,
    @SerializedName("streaming_hours") val streamingHours: String?,
    @SerializedName("recording_hours") val recordingHours: String?,
    @SerializedName("viewing_hours") val viewingHours: String?,
    @SerializedName("lti_room_id") val ltiRoomId: String?,
    @SerializedName("additional_data") val additionalData: String?,
    @SerializedName("user_modified") val userModified: String?,
    @SerializedName("user_created") val userCreated: Int?,
    @SerializedName("date_modified") val dateModified: String?,
    @SerializedName("date_created") val dateCreated: String?,
    @SerializedName("deleted") val deleted: String?,
    @SerializedName("conference_id") val conferenceId: String?,
    @SerializedName("conference_token_url") val conferenceTokenUrl: String?,
    @SerializedName("companyData") val companyData: CompanyData?
) {

    data class Instructor(
        @SerializedName("value") val value: String?
    )

    data class CompanyData(
        @SerializedName("id") val id: Long?,
        @SerializedName("name") val name: String?,
        @SerializedName("avatar") val avatar: String?,
        @SerializedName("branding") val branding: String?,
        @SerializedName("playlist_room_id") val playlistRoomId: String?,
        @SerializedName("quizzes_room_id") val quizzesRoomId: String?
    )

}
