package com.kme.kaltura.kmesdk.webrtc.peerconnection

import android.content.Intent
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.message.type.KmeSdpType

/**
 * An interface for actions related to p2p connection
 */
internal interface IKmePeerConnection {

    /**
     * Setting TURN server for RTC
     *
     * @param turnUrl url of a TURN server
     * @param turnUser username for TURN server
     * @param turnCred password for TURN server
     */
    fun setTurnServer(
        turnUrl: String,
        turnUser: String,
        turnCred: String
    )

    /**
     * Set preferred settings for establish p2p connection
     *
     * @param micEnabled flag for enable/disable micro
     * @param camEnabled flag for enable/disable camera
     * @param frontCamEnabled flag for enable/disable front camera
     */
    fun setPreferredSettings(
        micEnabled: Boolean,
        camEnabled: Boolean,
        frontCamEnabled: Boolean
    )

    /**
     * Setting view for stream rendering
     *
     * @param rendererView view to render on
     */
    fun setRenderer(rendererView: KmeSurfaceRendererView)

    /**
     * Remove renderer for viewer connection
     */
    fun removeRenderer()

    /**
     * Creates a local video preview
     */
    fun startPreview(previewRenderer: KmeSurfaceRendererView)

    /**
     * Creates p2p connection
     *
     * @param requestedUserIdStream id of a stream
     * @param isPublisher indicates is connection for publishing or viewing
     * @param useDataChannel indicates if data channel is used for speaking indication
     * @param listener listener for p2p events
     */
    fun createPeerConnection(
        requestedUserIdStream: String,
        isPublisher: Boolean,
        useDataChannel: Boolean,
        listener: IKmePeerConnectionClientEvents
    )

    /**
     * Starts screen share publishing
     *
     * @param requestedUserIdStream id of a stream
     * @param screenCaptureIntent media projection intent
     * @param listener listener for p2p events
     */
    fun startScreenShare(
        requestedUserIdStream: String,
        screenCaptureIntent: Intent,
        listener: IKmePeerConnectionClientEvents
    )

    /**
     * Setting media server id for data relay
     *
     * @param mediaServerId id of a server
     */
    fun setMediaServerId(mediaServerId: Long)

    /**
     * Creates an offers
     */
    fun createOffer()

    /**
     * Setting remote SDP
     *
     * @param type type of SDP
     * @param sdp string representation of SDP
     */
    fun setRemoteSdp(type: KmeSdpType, sdp: String)

    /**
     * Toggle camera
     *
     * @param isEnable flag to enable/disable camera
     */
    fun enableCamera(isEnable: Boolean)

    /**
     * Toggle audio
     *
     * @param isEnable flag to enable/disable audio
     */
    fun enableAudio(isEnable: Boolean)

    /**
     * Switch between existing cameras
     */
    fun switchCamera()

    /**
     * Closes actual p2p connection
     */
    fun disconnectPeerConnection()

}
