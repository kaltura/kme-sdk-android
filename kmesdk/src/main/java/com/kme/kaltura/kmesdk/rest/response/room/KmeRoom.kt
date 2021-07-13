package com.kme.kaltura.kmesdk.rest.response.room

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeSettings
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeSettingsV2

data class KmeRoom(
    @SerializedName("settings") val settings: KmeSettings?,
    @SerializedName("settings_v2") var settingsV2: KmeSettingsV2?
) : KmeBaseRoom()