package com.kme.kaltura.kmesdk.rest.response.room

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeSettingsV2

data class KmeChangeRoomSettingsResponse(
    @SerializedName("data")  override val data: KmeSettingsV2
) : KmeResponse()
