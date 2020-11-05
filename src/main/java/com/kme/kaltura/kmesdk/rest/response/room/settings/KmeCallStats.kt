package com.kme.kaltura.kmesdk.rest.response.room.settings

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class KmeCallStats {
    @SerializedName("app_id")
    @Expose
    var appId: String? = null

    @SerializedName("app_secret")
    @Expose
    var appSecret: String? = null
}