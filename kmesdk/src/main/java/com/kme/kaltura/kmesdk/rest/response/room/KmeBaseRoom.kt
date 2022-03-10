package com.kme.kaltura.kmesdk.rest.response.room

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

open class KmeBaseRoom : KmeResponseData(){

    @SerializedName("id")
    val id: Long = 0
    @SerializedName("name")
    val name: String? = null
    @SerializedName("is_ondemand")
    val isOnDemand: Boolean? = null
    @SerializedName("summary")
    val summary: String? = null
    @SerializedName("alias")
    val alias: String? = null
    @SerializedName("avatar")
    var avatar: String? = null
    @SerializedName("company_avatar")
    val companyAvatar: String? = null
    @SerializedName("company_id")
    val companyId: Long = 0
    @SerializedName("company_name")
    val companyName: String? = null
    @SerializedName("app_version")
    val appVersion: String? = null
    @SerializedName("status")
    val status: String? = null
    @SerializedName("room_user_role")
    val roomUserRole: String? = null
    @SerializedName("user_role")
    val userRole: String? = null
    @SerializedName("is_user_moderator")
    val isUserModerator: String? = null
    @SerializedName("instructors")
    val instructors: Map<String, Instructor>? = null
    @SerializedName("type")
    val type: String? = null
    @SerializedName("auto_upload_recordings")
    val autoUploadRecordings: Boolean? = null
    @SerializedName("is_turn_configured")
    val isTurnConfigured: Boolean? = null
    @SerializedName("privacy_level")
    val privacyLevel: String? = null
    @SerializedName("allow_guest_enter_selfpaced")
    val allowGuestEnterSelfPaced: Boolean? = null
    @SerializedName("allow_guest_enter_liveclass")
    val allowGuestEnterLiveClass: Boolean? = null
    @SerializedName("show_join_liveclass")
    val showJoinLiveClass: Boolean? = null
    @SerializedName("active_breakout_id")
    val activeBreakoutId: String? = null
    @SerializedName("file_hierarchy_id")
    val fileHierarchyId: String? = null
    @SerializedName("parent_room_id")
    val parentRoomId: String? = null
    @SerializedName("index_id")
    val indexId: String? = null
    @SerializedName("playlist_id")
    val playlistId: String? = null
    @SerializedName("default_playlist_id")
    val defaultPlaylistId: String? = null
    @SerializedName("migrate_log")
    val migrateLog: String? = null
    @SerializedName("playlist_migrate_status")
    val playlistMigrateStatus: String? = null
    @SerializedName("whiteboard_migrate_status")
    val whiteboardMigrateStatus: String? = null
    @SerializedName("chat_migrate_status")
    val chatMigrateStatus: String? = null
    @SerializedName("description")
    val description: String? = null
    @SerializedName("license_id")
    val licenseId: Int? = null
    @SerializedName("max_guests")
    val maxGuests: Int? = null
    @SerializedName("duration")
    val duration: Int? = null
    @SerializedName("server_id")
    val serverId: String? = null
    @SerializedName("streaming_hours")
    val streamingHours: String? = null
    @SerializedName("recording_hours")
    val recordingHours: String? = null
    @SerializedName("viewing_hours")
    val viewingHours: String? = null
    @SerializedName("lti_room_id")
    val ltiRoomId: String? = null
    @SerializedName("additional_data")
    val additionalData: String? = null
    @SerializedName("user_modified")
    val userModified: String? = null
    @SerializedName("user_created")
    val userCreated: Int? = null
    @SerializedName("date_modified")
    val dateModified: String? = null
    @SerializedName("date_created")
    val dateCreated: String? = null
    @SerializedName("deleted")
    val deleted: String? = null
    @SerializedName("conference_id")
    val conferenceId: String? = null
    @SerializedName("conference_token_url")
    val conferenceTokenUrl: String? = null
    @SerializedName("companyData")
    val companyData: CompanyData? = null
    @SerializedName("integrations")
    val integrations: KmeIntegrations? = null


    data class Instructor(
        @SerializedName("name") val name: String?
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
