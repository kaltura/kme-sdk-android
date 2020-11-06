package com.kme.kaltura.kmesdk.rest.response.room.settings

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class KmeCompanySettings {
    @SerializedName("is_secure_lti")
    @Expose
    var isSecureLti: String? = null

    @SerializedName("free_invites_count")
    @Expose
    var freeInvitesCount: Int? = null

    @SerializedName("filter_deleted_room_from_files")
    @Expose
    var filterDeletedRoomFromFiles: String? = null

    @SerializedName("file_share_feature")
    @Expose
    var fileShareFeature: String? = null

    @SerializedName("order_playlist_files_by_name")
    @Expose
    var orderPlaylistFilesByName: String? = null

    @SerializedName("allow_only_admins_to_manage_files_playlist")
    @Expose
    var allowOnlyAdminsToManageFilesPlaylist: String? = null

    @SerializedName("is_webhook_enabled")
    @Expose
    var isWebhookEnabled: String? = null

    @SerializedName("youtube_search_limit_free")
    @Expose
    var youtubeSearchLimitFree: Int? = null

    @SerializedName("youtube_search_limit_paid")
    @Expose
    var youtubeSearchLimitPaid: Int? = null

    @SerializedName("instructors_can_access_recordings")
    @Expose
    var instructorsCanAccessRecordings: String? = null

    @SerializedName("youtube_private_token")
    @Expose
    var youtubePrivateToken: String? = null

    @SerializedName("default_room_version")
    @Expose
    var defaultRoomVersion: String? = null

    @SerializedName("is_unlimited_account")
    @Expose
    var isUnlimitedAccount: String? = null

    @SerializedName("show_nr2_wel_msg")
    @Expose
    var showNr2WelMsg: String? = null
}