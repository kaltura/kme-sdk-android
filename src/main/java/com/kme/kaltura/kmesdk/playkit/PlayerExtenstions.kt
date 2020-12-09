package com.kme.kaltura.kmesdk.playkit

import com.kaltura.tvplayer.KalturaPlayer

fun String?.toPlayerTime(): Float {
    return (this?.toFloat() ?: 0f) * 1000
}

fun KalturaPlayer.isEnded(): Boolean {
    return this.currentPosition >= this.duration
}