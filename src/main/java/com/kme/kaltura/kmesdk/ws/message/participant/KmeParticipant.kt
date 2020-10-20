package com.kme.kaltura.kmesdk.ws.message.participant

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.permission.KmeUserPermissions
import com.kme.kaltura.kmesdk.ws.message.type.KmeLiveMediaState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmePlatformType
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserRole

data class KmeParticipant(

    @SerializedName("user_id") var userId: Long? = null,
    @SerializedName("region_id") var regionId: Long? = null,
    @SerializedName("user_type") var userType: String? = null,
    @SerializedName("avatar") var avatar: String? = null,
    @SerializedName("user_role") var userRole: KmeUserRole? = null,
    @SerializedName("full_name") var fullName: String? = null,
    @SerializedName("region_name") var regionName: String? = null,
    @SerializedName("join_time") var joinTime: Long? = null,
    @SerializedName("connection_state") var connectionState: String? = null,
    @SerializedName("live_media_state") var liveMediaState: KmeLiveMediaState? = null,
    @SerializedName("webcam_state") var webcamState: KmeMediaDeviceState? = null,
    @SerializedName("mic_state") var micState: KmeMediaDeviceState? = null,
    @SerializedName("time_hand_raised") var timeHandRaised: Long? = null,
    @SerializedName("last_unmute_time") var lastUnmuteTime: Long? = null,
    @SerializedName("device_type") var deviceType: KmePlatformType? = null,
    @SerializedName("browser") var browser: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("managing_server_id") var managingServerId: Long? = null,
    @SerializedName("out_of_tab_focus") var outOfTabFocus: Boolean? = null,
    @SerializedName("user_permissions") var userPermissions: KmeUserPermissions? = null,
    @SerializedName("is_moderator") var isModerator: Boolean? = null,
    @SerializedName("is_captioner") var isCaptioner: Boolean? = null,
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("long") var long: Double? = null

)