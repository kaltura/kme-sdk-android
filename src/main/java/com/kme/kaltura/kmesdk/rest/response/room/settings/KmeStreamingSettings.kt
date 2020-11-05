package com.kme.kaltura.kmesdk.rest.response.room.settings

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class KmeStreamingSettings {
    @SerializedName("video")
    @Expose
    var video: KmeVideo? = null
}