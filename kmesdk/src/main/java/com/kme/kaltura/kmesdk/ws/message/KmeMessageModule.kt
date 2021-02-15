package com.kme.kaltura.kmesdk.ws.message

import com.google.gson.annotations.SerializedName

enum class KmeMessageModule(
  @SerializedName("module") val moduleName: String
) {

    @SerializedName("RoomInit", alternate = ["roominit"])
    ROOM_INIT("RoomInit"),

    @SerializedName("Banners", alternate = ["banners"])
    BANNERS("Banners"),

    @SerializedName("RoomParticipants", alternate = ["roomparticipants"])
    ROOM_PARTICIPANTS("RoomParticipants"),

    @SerializedName("Chat", alternate = ["chat"])
    CHAT("Chat"),

    @SerializedName("Notes", alternate = ["notes"])
    NOTES("Notes"),

    @SerializedName("RoomSettings", alternate = ["roomsettings"])
    ROOM_SETTINGS("RoomSettings"),

    @SerializedName("ActiveContent", alternate = ["activecontent"])
    ACTIVE_CONTENT("ActiveContent"),

    @SerializedName("Video", alternate = ["video"])
    VIDEO("Video"),

    @SerializedName("SlidesPlayer", alternate = ["slidesplayer"])
    SLIDES_PLAYER("SlidesPlayer"),

    @SerializedName("Streaming", alternate = ["streaming"])
    STREAMING("Streaming"),

    @SerializedName("Whiteboard", alternate = ["whiteboard"])
    WHITEBOARD("Whiteboard"),

    @SerializedName("Recording", alternate = ["recording"])
    RECORDING("Recording"),

    @SerializedName("DesktopShare", alternate = ["desktopshare"])
    DESKTOP_SHARE("DesktopShare");

    override fun toString(): String {
        return moduleName.toLowerCase()
    }

}