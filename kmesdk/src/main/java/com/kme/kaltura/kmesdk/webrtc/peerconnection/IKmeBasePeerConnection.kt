package com.kme.kaltura.kmesdk.webrtc.peerconnection

import android.content.Context
import org.webrtc.EglBase
import org.webrtc.IceCandidate

/**
 * An interface for actions under WebRTC peer connection object
 */
// TODO: split by different interfaces
interface IKmeBasePeerConnection {

    /**
     * Creates peer connection factory
     *
     * @param context application context
     * @param events listener for events from [IKmePeerConnectionEvents]
     */
    fun createPeerConnectionFactory(
        context: Context,
        events: IKmePeerConnectionEvents
    )

    /**
     * Getting rendering context for WebRTC
     *
     * @return rendering context
     */
    fun getRenderContext(): EglBase.Context?

    /**
     * Handle adding ICE candidates
     *
     * @param candidate ICE candidate to add
     */
    fun addRemoteIceCandidate(candidate: IceCandidate?)

    /**
     * Handle remove remote ICE candidates
     *
     * @param candidates ICE candidates to remove
     */
    fun removeRemoteIceCandidates(candidates: Array<IceCandidate>)

    /**
     * Closes actual connection
     */
    fun close()

}
