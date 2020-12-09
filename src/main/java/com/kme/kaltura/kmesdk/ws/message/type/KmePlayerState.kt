package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmePlayerState(
    @SerializedName("player_state") val playerState: String
) {

    @SerializedName("PLAY")
    PLAY("PLAY"),

    @SerializedName("PLAYING")
    PLAYING("PLAYING"),

    @SerializedName("PAUSED")
    PAUSED("PAUSED"),

    @SerializedName("ENDED")
    ENDED("ENDED"),

    @SerializedName("SEEK_TO")
    SEEK_TO("SEEK_TO"),

    @SerializedName("PAUSE")
    PAUSE("PAUSE")

}