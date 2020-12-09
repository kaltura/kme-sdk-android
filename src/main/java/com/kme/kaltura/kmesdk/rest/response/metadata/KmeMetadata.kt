package com.kme.kaltura.kmesdk.rest.response.metadata

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

data class KmeMetadata(
    @SerializedName("version") val version : String?,
    @SerializedName("legacy_domain") val legacyDomain : String?,
    @SerializedName("files_url") val filesUrl : String?,
    @SerializedName("st_public") val st_public : String?,
    @SerializedName("firstTimeVideo") val firstTimeVideo : String?,
    @SerializedName("callstatsEnabled") val callstatsEnabled : Boolean?,
    @SerializedName("qualityMeterAppearance") val qualityMeterAppearance : Boolean?,
    @SerializedName("availableLanguages") val availableLanguages : List<Language>?,
    @SerializedName("rtcSamplesAmount") val rtcSamplesAmount : Int?,
    @SerializedName("rtcSamplesInterval") val rtcSamplesInterval : Int?
) : KmeResponseData() {

    data class Language(
        @SerializedName("language_id") val languageId : String,
        @SerializedName("name") val name : String
    )

}
