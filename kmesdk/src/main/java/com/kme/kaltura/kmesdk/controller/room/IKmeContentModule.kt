package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.content.KmeContentView

/**
 * An interface for content sharing
 */
interface IKmeContentModule {

    /**
     * Subscribing for the room events related to content sharing
     */
    fun subscribe(listener: KmeContentListener)

    /**
     * Un-subscribing from the room events related to content sharing
     */
    fun unsubscribe()

    /**
     * Content share listener
     */
    interface KmeContentListener {
        /**
         * Fired once content is not available
         *
         * @param view view for show actual content
         */
        fun onContentAvailable(view: KmeContentView)

        /**
         * Fired once content is not available
         */
        fun onContentNotAvailable()
    }

}
