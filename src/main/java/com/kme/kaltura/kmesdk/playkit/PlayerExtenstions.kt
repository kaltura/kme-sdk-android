package com.kme.kaltura.kmesdk.playkit

import com.kaltura.playkit.PlayerEvent
import com.kaltura.tvplayer.KalturaPlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.*

fun KalturaPlayer.isEnded(): Boolean {
    return this.currentPosition >= this.duration
}

fun PlayerConstants.PlayerState.asKalturaEvent(): PlayerEvent? {
    return when (this) {
        UNKNOWN, UNSTARTED, BUFFERING -> null
        VIDEO_CUED -> PlayerEvent.Generic(PlayerEvent.Type.CAN_PLAY)
        ENDED -> PlayerEvent.Generic(PlayerEvent.Type.ENDED)
        PLAYING -> PlayerEvent.Generic(PlayerEvent.Type.PLAYING)
        PAUSED -> PlayerEvent.Generic(PlayerEvent.Type.PAUSE)
    }
}