package com.kme.kaltura.kmesdk.ws.message.type

import com.google.gson.annotations.SerializedName

enum class KmePlayerState(
    @SerializedName("player_state") val playerState: String
) {

    @SerializedName("PLAY", alternate = ["play"])
    PLAY("PLAY"),

    @SerializedName("PLAYING", alternate = ["playing"])
    PLAYING("PLAYING"),

    @SerializedName("PAUSED", alternate = ["paused"])
    PAUSED("PAUSED"),

    @SerializedName("ENDED", alternate = ["ended"])
    ENDED("ENDED"),

    @SerializedName("STOP", alternate = ["stop"])
    STOP("STOP"),

    @SerializedName("SEEK_TO", alternate = ["seek_to"])
    SEEK_TO("SEEK_TO"),

    @SerializedName("PAUSE", alternate = ["pause"])
    PAUSE("PAUSE")

}