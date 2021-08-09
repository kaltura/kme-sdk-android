package com.kme.kaltura.kmesdk.ws.message.type.permissions

import com.google.gson.annotations.SerializedName

enum class KmePermissionValue(value: String) {

    @SerializedName("DEFAULT")
    DEFAULT("DEFAULT"),

    @SerializedName("PERMISSIONS_SET")
    PERMISSIONS_SET("PERMISSIONS_SET"),

    @SerializedName("PERMISSIONS_UNSET")
    PERMISSIONS_UNSET("PERMISSIONS_UNSET"),

    @SerializedName("UNSET")
    UNSET("UNSET"),

    @SerializedName("on")
    ON("on"),

    @SerializedName("off")
    OFF("off"),

    @SerializedName("standard")
    STANDARD("standard"),

    @SerializedName("hard")
    HARD("hard"),

    @SerializedName("ONLY_MODERATORS")
    ONLY_MODERATORS("ONLY_MODERATORS"),

    @SerializedName("virtual_class")
    VIRTUAL_CLASS("virtual_class"),

    @SerializedName("webinar")
    WEBINAR("webinar")

}