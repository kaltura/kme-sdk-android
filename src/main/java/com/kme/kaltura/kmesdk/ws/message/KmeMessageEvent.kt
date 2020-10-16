package com.kme.kaltura.kmesdk.ws.message

import com.google.gson.annotations.SerializedName

enum class KmeMessageEvent(
    @SerializedName("name") val moduleName: String
) {

    @SerializedName("joinRoom", alternate = ["joinroom"])
    JOIN_ROOM("joinRoom"),

    @SerializedName("instructorIsOffLine", alternate = ["instructorisoffline"])
    INSTRUCTOR_IS_OFFLINE("instructorIsOffLine"),

    @SerializedName("joinedRoom", alternate = ["joinedroom"])
    JOINED_ROOM("joinedRoom"),

    @SerializedName("closeWebSocket", alternate = ["closewebsocket"])
    CLOSE_WEB_SOCKET("closeWebSocket"),

    @SerializedName("roomState", alternate = ["roomstate"])
    ROOM_STATE("roomState"),

    @SerializedName("userMediaStateInit", alternate = ["usermediastateinit"])
    MEDIA_INIT("userMediaStateInit"),

    @SerializedName("startPublishing", alternate = ["startpublishing"])
    START_PUBLISHING("startPublishing");

    override fun toString(): String {
        return moduleName.toLowerCase()
    }

}