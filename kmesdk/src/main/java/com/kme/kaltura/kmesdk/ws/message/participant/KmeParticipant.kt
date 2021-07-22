package com.kme.kaltura.kmesdk.ws.message.participant

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeSettingsV2
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmePlatformType
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserRole
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeParticipant(

    @SerializedName("user_id") var userId: Long? = null,
    @SerializedName("region_id") var regionId: Long? = null,
    @SerializedName("user_type") var userType: KmeUserType? = null,
    @SerializedName("avatar") var avatar: String? = null,
    @SerializedName("user_role") var userRole: KmeUserRole? = null,
    @SerializedName("full_name") var fullName: String? = null,
    @SerializedName("region_name") var regionName: String? = null,
    @SerializedName("join_time") var joinTime: Long? = null,
    @SerializedName("connection_state") var connectionState: String? = null,
    @SerializedName("live_media_state") var liveMediaState: KmeMediaDeviceState? = null,
    @SerializedName("webcam_state") var webcamState: KmeMediaDeviceState? = null,
    @SerializedName("mic_state") var micState: KmeMediaDeviceState? = null,
    @SerializedName("time_hand_raised") var timeHandRaised: Long = 0,
    @SerializedName("last_unmute_time") var lastUnmuteTime: Long? = null,
    @SerializedName("device_type") var deviceType: KmePlatformType? = null,
    @SerializedName("browser") var browser: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("managing_server_id") var managingServerId: Long? = null,
    @SerializedName("out_of_tab_focus") var outOfTabFocus: Boolean? = null,
    @SerializedName("user_permissions") var userPermissions: KmeSettingsV2? = null,
    @SerializedName("is_moderator") var isModerator: Boolean?,
    @SerializedName("is_captioner") var isCaptioner: Boolean?,
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("long") var long: Double? = null,

//    Local
    var isSpeaking: Boolean = false

) : Parcelable {

    fun isDynamicAvatar() = avatar?.startsWith("Users/d/userAvatars") ?: false

}