package com.kme.kaltura.kmesdk.rest.response.room.settings

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class KmeVideo {
    @SerializedName("frameRate")
    @Expose
    var frameRate: KmeFrameRate? = null
}