package com.kme.kaltura.kmesdk.rest.response.room

import com.google.gson.annotations.SerializedName

data class KmeIntegrations(
    @SerializedName("kaltura") val kaltura: Kaltura?
) {

    data class Kaltura(
        @SerializedName("user_company") val userCompany: UserCompany? = null,
        @SerializedName("room") val room: Room? = null,
        @SerializedName("company") val company: Company? = null
    ) {

        data class Room(
            @SerializedName("tokens") val tokens: String? = null,
            @SerializedName("settings") val settings: Settings? = null,
            @SerializedName("id") val id: String? = null
        ) {
            data class Settings(
                @SerializedName("modules") val modules: String? = null,
                @SerializedName("kaltura_privacy_context") val privacyContext: String? = null,
                @SerializedName("custom_kaltura_room_type") val roomType: String? = null
            )
        }

        data class UserCompany(
            @SerializedName("tokens") val tokens: String? = null,
//            @SerializedName("settings") val settings: List<Any>? = null,
            @SerializedName("id") val id: String? = null
        )

        data class Company(
            @SerializedName("tokens") val tokens: String? = null,
//            @SerializedName("settings") val settings: List<Any>? = null,
            @SerializedName("id") val id: String? = null
        )

    }

}