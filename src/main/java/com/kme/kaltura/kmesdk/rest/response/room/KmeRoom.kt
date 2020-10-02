package com.kme.kaltura.kmesdk.rest.response.room

import com.google.gson.annotations.SerializedName

data class KmeRoom(
    val id: Long? = null,
    val name: String? = null,
    @SerializedName("is_ondemand")
    val isOnDemand: Boolean? = null,
    val summary: String? = null,
    val alias: String? = null,
    val avatar: String? = null,
    @SerializedName("company_id")
    val companyId: Long? = null,
    @SerializedName("app_version")
    val appVersion: String? = null,
    val status: String? = null,
    @SerializedName("room_user_role")
    val roomUserRole: String? = null,
    val instructors: Instructor? = null
) {

    data class Instructor(
        val value: String? = null
    )

}
