package com.kme.kaltura.kmesdk.ws.message.type.permissions

import com.google.gson.annotations.SerializedName

enum class KmeModuleVisibilityValue(value: String) {

    @SerializedName("ALL")
    ALL("ALL"),

    @SerializedName("INSTRUCTORS")
    INSTRUCTORS("INSTRUCTORS"),

    @SerializedName("MODERATORS")
    MODERATORS("MODERATORS"),

    @SerializedName("GUESTS_ONLY")
    UNSET("GUESTS_ONLY"),

    @SerializedName("NONE")
    NONE("NONE")

}