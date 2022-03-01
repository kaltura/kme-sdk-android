package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.content.KmeContentView
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType

/**
 * An interface for content sharing
 */
interface IKmeContentModule : IKmeModule {

    /**
     * Subscribing for the room events related to content sharing
     */
    fun subscribe(listener: KmeContentListener)

    /**
     * Un-subscribing from the room events related to content sharing
     */
    fun unsubscribe()

    /**
     * Setting result of screen projection permission from MediaProjectionManager
     * Fired once application provides permission result to the KmeSDK
     */
    fun onScreenSharePermission(approved: Boolean)

    /**
     * Mute/Un-mute presented audio
     *
     * @param isMute
     */
    fun muteActiveContent(isMute: Boolean)

    /**
     * Destroy active content
     *
     * @param isMute
     */
    fun destroy()

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
         * @param type for content
         */
        fun onContentNotAvailable(type: KmeContentType?)
    }

}
