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

    @SerializedName("Streaming", alternate = ["streaming"])
    STREAMING("Streaming");

    override fun toString(): String {
        return moduleName.toLowerCase()
    }
}