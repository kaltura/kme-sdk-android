package com.kme.kaltura.kmesdk.content.playkit

import com.kaltura.netkit.utils.ErrorElement

/**
 * Interface definition for a callback to be invoked when
 * Kaltura Player is unable to load the remote media.
 */
interface OnLoadKalturaErrorListener {

    /**
     * Callback method to be invoked when [com.kaltura.tvplayer.KalturaPlayer] can't load a media.
     * Called only from the Kaltura Player OVP implementation.
     *
     * @see [com.kaltura.tvplayer.KalturaPlayer.loadMedia]
     */
    fun onLoadKalturaMediaError(error: ErrorElement)

}