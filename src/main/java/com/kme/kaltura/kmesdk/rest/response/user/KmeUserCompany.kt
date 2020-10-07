package com.kme.kaltura.kmesdk.rest.response.user

import com.google.gson.annotations.SerializedName

data class KmeUserCompany(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String?,
    @SerializedName("avatar") var avatar: String?,
    @SerializedName("allow_add_courses") val allowAddCourses: Boolean?,
    @SerializedName("is_paying_company") val isPayingCompany: Boolean?,
    @SerializedName("recording_reminder_ind") val recordingReminder: Int?,
    @SerializedName("max_rooms") val maxRooms: Int?,
    @SerializedName("role") val role: String?,
    @SerializedName("playlist_room_id") val playlistRoomId: String?,
    @SerializedName("quizzes_room_id") val quizzesRoomId: String?,
    @SerializedName("branding") val branding: Branding?,
    @SerializedName("default_lang") val defaultLang: String?,
    @SerializedName("feature_flags") val featureFlags: FeatureFlags?,
//    @SerializedName("settings") val settings: Long?,
//    @SerializedName("settings_v2") val settingsV2: Long?,
//    @SerializedName("extras") val extras: Long?,
    @SerializedName("custom_order_id") val customOrderId: Long?
) {

    data class Branding(
        @SerializedName("background_color") val backgroundColor: String?,
        @SerializedName("text_color") val textColor: String?
    )

    data class FeatureFlags(
        @SerializedName("data_channel") val dataChannel: Boolean?,
        @SerializedName("ice_negotiation_flow") val iceNegotiationFlow: Boolean?,
        @SerializedName("ice_ipv6") val iceIpv6: Boolean?,
        @SerializedName("nr2_rooms") val nr2Rooms: Boolean?,
        @SerializedName("audio_slides") val audioSlides: Boolean?,
        @SerializedName("streams_watchdog") val streamsWatchdog: Boolean?,
        @SerializedName("is_janus_dump") val isJanusDump: Boolean?,
        @SerializedName("mathjax_enabled") val mathjaxEnabled: Boolean?,
        @SerializedName("whiteboard_snapshot") val whiteboardSnapshot: Boolean?,
        @SerializedName("ios_safari_stuck_video_watchdog") val iosSafariStuckVideoWatchdog: Boolean?,
        @SerializedName("phone_bridge") val phoneBridge: Boolean?,
        @SerializedName("youtube_search") val youtubeSearch: Boolean?,
    )

}
