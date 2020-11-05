package com.kme.kaltura.kmesdk.rest.response.room.settings

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class KmeRoomAccessModule {
    @SerializedName("default_settings")
    @Expose
    var defaultSettings: KmeDefaultSettings? = null
}