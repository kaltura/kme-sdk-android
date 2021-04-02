package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView

/**
 * An interface for desktop share actions
 */
interface IKmeDesktopShareModule {

    /**
     * Start listen desktop share events
     *
     * @param renderer view for video rendering
     * @param callback callback for desktop share events
     */
    fun startListenDesktopShare(
        renderer: KmeSurfaceRendererView,
        callback: KmeDesktopShareEvents
    )

    /**
     * Stop listen desktop share events
     */
    fun stopListenDesktopShare()

    /**
     * Desktop share events
     */
    interface KmeDesktopShareEvents {

        /**
         * Triggered by administrator
         *
         * @param isActive indicates is desktop share currently active
         */
        fun onDesktopShareActive(isActive: Boolean)

        /**
         * Fired once administrator started to publish
         */
        fun onDesktopShareAvailable()

        /**
         * Triggered by administrator
         *
         * @param isHd indicates that quality of video was changed
         */
        fun onDesktopShareQualityChanged(isHd: Boolean)
    }

}
