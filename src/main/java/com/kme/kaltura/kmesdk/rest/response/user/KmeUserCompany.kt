package com.kme.kaltura.kmesdk.rest.response.user

import com.google.gson.annotations.SerializedName

data class KmeUserCompany(
    val id: Long? = null,
    val name: String? = null,
    val avatar: String? = null,
    @SerializedName("allow_add_courses")
    val allowAddCourses: Boolean? = null,
    @SerializedName("is_paying_company")
    val isPayingCompany: Boolean? = null,
    @SerializedName("recording_reminder_ind")
    val recordingReminder: Int? = null,
    @SerializedName("max_rooms")
    val maxRooms: Int? = null,
    val role: String? = null,
    @SerializedName("playlist_room_id")
    val playlistRoomId: String? = null,
    @SerializedName("quizzes_room_id")
    val quizzesRoomId: String? = null,
    val branding: Branding? = null,
    @SerializedName("default_lang")
    val defaultLang: String? = null,
    @SerializedName("feature_flags")
    val featureFlags: FeatureFlags? = null,
//    val settings: Long? = null,
//    @SerializedName("settings_v2")
//    val settingsV2: Long? = null,
//    val extras: Long? = null,
    @SerializedName("custom_order_id")
    val customOrderId: Long? = null
) {

    data class Branding(
        @SerializedName("background_color")
        val backgroundColor: String? = null,
        @SerializedName("text_color")
        val textColor: String? = null
    )

    data class FeatureFlags(
        @SerializedName("data_channel")
        val dataChannel: Boolean? = null,
        @SerializedName("ice_negotiation_flow")
        val iceNegotiationFlow: Boolean? = null,
        @SerializedName("ice_ipv6")
        val iceIpv6: Boolean? = null,
        @SerializedName("nr2_rooms")
        val nr2Rooms: Boolean? = null,
        @SerializedName("audio_slides")
        val audioSlides: Boolean? = null,
        @SerializedName("streams_watchdog")
        val streamsWatchdog: Boolean? = null,
        @SerializedName("is_janus_dump")
        val isJanusDump: Boolean? = null,
        @SerializedName("mathjax_enabled")
        val mathjaxEnabled: Boolean? = null,
        @SerializedName("whiteboard_snapshot")
        val whiteboardSnapshot: Boolean? = null,
        @SerializedName("ios_safari_stuck_video_watchdog")
        val iosSafariStuckVideoWatchdog: Boolean? = null,
        @SerializedName("phone_bridge")
        val phoneBridge: Boolean? = null,
        @SerializedName("youtube_search")
        val youtubeSearch: Boolean? = null,
    )

}
