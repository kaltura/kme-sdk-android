package com.kme.kaltura.kmesdk.webrtc.peerconnection

interface IKmePeerConnectionClientEvents {

    /**
     * Callback fired once peerConnection instance created
     */
    fun onPeerConnectionCreated(userId: Long)

    /**
     * Callback fired once local SDP is created and set.
     */
    fun onLocalDescription(userId: Long, mediaServerId: Long, sdp: String, type: String)

    /**
     * Callback fired once local Ice candidate is generated.
     */
    fun onIceCandidate(candidate: String)

    /**
     * Callback fired once local ICE candidates are removed.
     */
    fun onIceCandidatesRemoved(candidates: String)

    /**
     * Callback fired once connection is established (IceConnectionState is
     * CONNECTED).
     */
    fun onIceConnected()

    /**
     * Callback fired once ice gathering is complete (IceGatheringDone is COMPLETE).
     */
    fun onIceGatheringDone(userId: Long, mediaServerId: Long)

    /**
     * Callback fired to indicate current talking user
     */
    fun onUserSpeaking(userId: Long, isSpeaking: Boolean)

    /**
     * Callback fired once connection is closed (IceConnectionState is
     * DISCONNECTED).
     */
    fun onIceDisconnected()

    /**
     * Callback fired once peer connection is closed.
     */
    fun onPeerConnectionClosed()

    /**
     * Callback fired once peer connection statistics is ready.
     */
    fun onPeerConnectionStatsReady(reports: String)

    /**
     * Callback fired once peer connection error happened.
     */
    fun onPeerConnectionError(description: String)

}
