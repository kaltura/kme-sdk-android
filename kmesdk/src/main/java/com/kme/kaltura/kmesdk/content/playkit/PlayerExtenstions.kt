package com.kme.kaltura.kmesdk.content.playkit

import com.kaltura.playkit.PlayerEvent
import com.kaltura.tvplayer.KalturaPlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.*

/**
 * Check is playback of media file is ended
 */
fun KalturaPlayer.isEnded(): Boolean {
    return this.currentPosition >= this.duration
}

/**
 * Wrap [com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState]
 * as [com.kaltura.playkit.PlayerEvent]
 */
fun PlayerConstants.PlayerState.asKalturaEvent(): PlayerEvent? {
    return when (this) {
        VIDEO_CUED -> PlayerEvent.Generic(PlayerEvent.Type.CAN_PLAY)
        ENDED -> PlayerEvent.Generic(PlayerEvent.Type.ENDED)
        PLAYING -> PlayerEvent.Generic(PlayerEvent.Type.PLAYING)
        PAUSED -> PlayerEvent.Generic(PlayerEvent.Type.PAUSE)
        else -> null
    }
}