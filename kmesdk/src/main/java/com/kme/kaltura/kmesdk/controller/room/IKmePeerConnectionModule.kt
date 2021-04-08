package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView

/**
 * An interface for wrap actions with [IKmePeerConnection]
 */
interface IKmePeerConnectionModule: IKmePeerConnectionClientEvents {

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
     * Creates a video preview
     */
    fun startPreview(previewRenderer: KmeSurfaceRendererView)

    /**
     * Stops a video preview
     */
    fun stopPreview()

    /**
     * Creates publisher connection
     *
     * @param requestedUserIdStream id of a user (publisher)
     * @param renderer view for video rendering
     */
    fun addPublisher(
        requestedUserIdStream: String,
        renderer: KmeSurfaceRendererView,
        micEnabled: Boolean,
        camEnabled: Boolean,
        frontCamEnabled: Boolean
    )

    /**
     * Creates a viewer connection
     *
     * @param requestedUserIdStream id of a user (stream)
     * @param renderer view for video rendering
     */
    fun addViewer(
        requestedUserIdStream: String,
        renderer: KmeSurfaceRendererView
    )

    /**
     * Getting publishing state
     *
     * @return [Boolean] value that indicates publishing state
     */
    fun isPublishing(): Boolean

    /**
     * Toggle publisher's camera
     *
     * @param isEnable flag to enable/disable camera
     */
    fun enableCamera(isEnable: Boolean)

    /**
     * Toggle publisher's audio
     *
     * @param isEnable flag to enable/disable audio
     */
    fun enableAudio(isEnable: Boolean)

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

}
