package com.kme.kaltura.kmesdk.rest.response.room.settings

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
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
    @SerializedName("connection_state_timeout") val connectionTimeout: Int?,
    @SerializedName("watchdog_for_recorders") val watchdogForRecorders: Boolean?,
    @SerializedName("nr2_mute_with_track_remove") val nr2MiteWithTrackRemove: Boolean?,
    @SerializedName("nr2_mute_video_element_on_mute") val nr2MuteVideoOnMute: Boolean?,
    @SerializedName("nr2_data_channel_via_rs") val nr2DataChannelViaRs: Boolean?
) : Parcelable
