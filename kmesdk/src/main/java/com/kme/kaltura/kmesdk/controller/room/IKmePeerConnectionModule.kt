package com.kme.kaltura.kmesdk.controller.room

import android.content.Intent
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState

/**
 * An interface for wrap actions with [IKmePeerConnection]
 */
interface IKmePeerConnectionModule : IKmePeerConnectionClientEvents {

    /**
     * Setting initialization data to the module
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param listener callback with [KmePeerConnectionEvents] for indicating main events
     */
    fun initialize(
        roomId: Long,
        companyId: Long,
        listener: KmePeerConnectionEvents
    )

    /**
     * Setting initialization data to the module
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param listener callback with [KmePeerConnectionEvents] for indicating main events
     * @param screenShareEvents callback with [KmeScreenShareEvents] for indicating screen share events
     */
    fun initialize(
        roomId: Long,
        companyId: Long,
        listener: KmePeerConnectionEvents,
        screenShareEvents: KmeScreenShareEvents
    )

    /**
     * Creates a video preview
     *
     * @param previewRenderer view for video rendering
     */
    fun startPreview(previewRenderer: KmeSurfaceRendererView)

    /**
     * Stops a video preview
     */
    fun stopPreview()

    /**
     * Join the room without publishing
     *
     * @param requestedUserIdStream id of a user (publisher)
     * @param liveState default live state
     * @param micState default mic state
     * @param camState default cam state
     */
    fun startLive(
        requestedUserIdStream: String,
        liveState: KmeMediaDeviceState,
        micState: KmeMediaDeviceState,
        camState: KmeMediaDeviceState
    )

    /**
     * Creates publisher connection
     *
     * @param requestedUserIdStream id of a user (publisher)
     * @param liveState default live state
     * @param micState default mic state
     * @param camState default cam state
     * @param frontCamEnabled flag for usage front camera
     */
    fun addPublisherConnection(
        requestedUserIdStream: String,
        liveState: KmeMediaDeviceState,
        micState: KmeMediaDeviceState,
        camState: KmeMediaDeviceState,
        frontCamEnabled: Boolean
    )

    /**
     * Creates a viewer connection
     *
     * @param requestedUserIdStream id of a user (stream)
     */
    fun addViewerConnection(requestedUserIdStream: String)

    /**
     * Getting publishing state
     *
     * @return [Boolean] value that indicates publishing state
     */
    fun isPublishing(): Boolean

    /**
     * Add renderer for publisher connection
     *
     * @param renderer view for video rendering
     */
    fun setPublisherRenderer(renderer: KmeSurfaceRendererView)

    /**
     * Add renderer for viewer connection
     *
     * @param requestedUserIdStream id of a user (stream)
     * @param renderer view for video rendering
     */
    fun setViewerRenderer(
        requestedUserIdStream: String,
        renderer: KmeSurfaceRendererView
    )

    /**
     * Remove renderer for publisher connection
     */
    fun removePublisherRenderer()

    /**
     * Remove renderer for viewer connection
     *
     * @param requestedUserIdStream id of a user (stream)
     */
    fun removeViewerRenderer(
        requestedUserIdStream: String
    )

    /**
     * Asking for screen permission from MediaProjectionManager
     */
    fun askForScreenSharePermission()

    /**
     * Set status from MediaProjectionManager
     *
     * @param resultCode result code returned by the child activity through its setResult()
     * @param screenCaptureIntent media projection intent
     */
    fun setScreenSharePermission(
        resultCode: Int,
        screenCaptureIntent: Intent
    )

    /**
     * Stops screen share publishing
     */
    fun stopScreenShare()

    /**
     * Add renderer for screen sharer connection
     *
     * @param renderer view for video rendering
     */
    fun setScreenShareRenderer(renderer: KmeSurfaceRendererView)

    /**
     * Toggle publisher's camera
     *
     * @param isEnable flag to enable/disable camera
     * @param silent allows to skip server message
     */
    fun enableCamera(
        isEnable: Boolean,
        silent: Boolean = false
    )

    /**
     * Toggle publisher's audio
     *
     * @param isEnable flag to enable/disable audio
     * @param silent allows to skip server message
     */
    fun enableAudio(
        isEnable: Boolean,
        silent: Boolean = false
    )

    /**
     * Toggle all viewer's audio
     *
     * @param isEnable flag to enable/disable audio
     */
    fun enableViewersAudio(isEnable: Boolean)

    /**
     * Switch between publisher's existing cameras
     */
    fun switchCamera()

    /**
     * Disconnect publisher/viewer connection by id
     *
     * @param requestedUserIdStream id of a stream
     */
    fun disconnect(requestedUserIdStream: String)

    /**
     * Disconnect all publisher/viewers connections
     */
    fun disconnectAll()

    /**
     * Peer connection events
     */
    interface KmePeerConnectionEvents {

        /**
         * Callback fired once publisher's stream is active on other side
         */
        fun onPublisherReady()

        /**
         * Callback fired once viewer's stream is active on client side
         *
         * @param id id of a user (stream)
         */
        fun onViewerReady(id: String)

        /**
         * Callback fired to indicate current talking user
         *
         * @param id id of a user (stream)
         * @param isSpeaking indicates is user currently speaking
         */
        fun onUserSpeaking(id: String, isSpeaking: Boolean)

        /**
         * Callback fired once peer connection removed
         *
         * @param id id of a user (stream)
         */
        fun onPeerConnectionRemoved(id: String)

        /**
         * Callback fired once peer connection error happens
         *
         * @param id id of a user (stream)
         * @param description error description
         */
        fun onPeerConnectionError(id: String, description: String)
    }

    /**
     * Screen share event
     */
    interface KmeScreenShareEvents {

        /**
         * Callback fired once KmeSDK need for screen permission from MediaProjectionManager
         */
        fun onAskForScreenSharePermission()
    }

}
