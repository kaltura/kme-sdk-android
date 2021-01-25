package com.kme.kaltura.kmesdk.webrtc.peerconnection

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import org.webrtc.StatsReport

/**
 * An interface for internal p2p connection events
 */
interface IKmePeerConnectionEvents {

    /**
     * Callback fired once peerConnection instance created
     */
    fun onPeerConnectionCreated()

    /**
     * Callback fired once local SDP is created and set
     *
     * @param sdp session description
     */
    fun onLocalDescription(sdp: SessionDescription)

    /**
     * Callback fired once local Ice candidate is generated
     *
     * @param candidate ICE candidate
     */
    fun onIceCandidate(candidate: IceCandidate)

    /**
     * Callback fired once local ICE candidates are removed
     *
     * @param candidates collection of ICE candidates
     */
    fun onIceCandidatesRemoved(candidates: Array<IceCandidate>)

    /**
     * Callback fired once connection is established (IceConnectionState is
     * CONNECTED)
     */
    fun onIceConnected()

    /**
     * Callback fired once ice gathering is complete (IceGatheringDone is COMPLETE)
     */
    fun onIceGatheringDone()

    /**
     * Callback fired to indicate current talking user
     *
     * @param isSpeaking indicates is user currently speaking
     */
    fun onUserSpeaking(isSpeaking: Boolean)

    /**
     * Callback fired once connection is closed (IceConnectionState is
     * DISCONNECTED)
     */
    fun onIceDisconnected()

    /**
     * Callback fired once peer connection is closed
     */
    fun onPeerConnectionClosed()

    /**
     * Callback fired once peer connection statistics is ready
     *
     * @param reports collection of stats
     */
    fun onPeerConnectionStatsReady(reports: Array<StatsReport>)

    /**
     * Callback fired once peer connection error happened
     *
     * @param description string representation of error reason
     */
    fun onPeerConnectionError(description: String)

}
