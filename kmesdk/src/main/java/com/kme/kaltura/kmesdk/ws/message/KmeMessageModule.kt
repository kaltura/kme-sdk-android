package com.kme.kaltura.kmesdk.ws.message

import android.annotation.SuppressLint
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

    @SerializedName("kaltura")
    KALTURA("kaltura"),

    @SerializedName("video", alternate = ["Video"])
    VIDEO("video"),

    @SerializedName("audio", alternate = ["Audio"])
    AUDIO("audio"),

    @SerializedName("youtube", alternate = ["Youtube"])
    YOUTUBE("youtube"),

    @SerializedName("SlidesPlayer", alternate = ["slidesplayer"])
    SLIDES_PLAYER("SlidesPlayer"),

    @SerializedName("streaming", alternate = ["streaming"])
    STREAMING("streaming"),

    @SerializedName("Whiteboard", alternate = ["whiteboard"])
    WHITEBOARD("Whiteboard"),

    @SerializedName("Recording", alternate = ["recording"])
    RECORDING("Recording"),

    @SerializedName("quickPoll", alternate = ["quickpoll"])
    QUICK_POLL("quickPoll"),

    @SerializedName("DesktopShare", alternate = ["desktopshare"])
    DESKTOP_SHARE("DesktopShare"),

    @SerializedName("breakout", alternate = ["breakout"])
    BREAKOUT("breakout"),

    @SerializedName("xl-room", alternate = ["xl-room"])
    XL_ROOM("xl-room");

    @SuppressLint("DefaultLocale")
    override fun toString(): String {
        return moduleName.lowercase()
    }

}
