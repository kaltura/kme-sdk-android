package com.kme.kaltura.kmesdk.rest.response.room

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData
import com.kme.kaltura.kmesdk.rest.response.room.settings.FeatureFlags

data class KmeWebRTCServer(
    @SerializedName("WSS_URL") val wssUrl: String?,
    @SerializedName("GUEST_LINK") val guestLink: String?,
    @SerializedName("TURN_URL") val turnUrl: String?,
    @SerializedName("ICE_SERVER_GROUP") val iceServerGroup: Int?,
    @SerializedName("TURN_USERNAME") val turnUsername: String?,
    @SerializedName("TURN_CREDENTIAL") val turnCredential: String?,
    @SerializedName("room_info") var roomInfo: KmeRoom?,
    @SerializedName("firebase_token") val firebaseToken: String?,
    @SerializedName("callstats") val callStats: CallStats?,
    @SerializedName("feature_flags") val featureFlags: FeatureFlags?,
    @SerializedName("tk") val tk: String?,
    @SerializedName("lang") val lang: String?,
    @SerializedName("server_time") val serverTime: Int?,
    @SerializedName("show_welcome_message") val showWelcomeMessage: Int?,
    @SerializedName("redis-key") val redisKey: String?,
    @SerializedName("token") val token: String?
) : KmeResponseData() {

    data class CallStats(
        @SerializedName("app_id") val appId: String?,
        @SerializedName("app_secret") val appSecret: String?
    )

}
