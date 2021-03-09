package com.kme.kaltura.kmesdk.webrtc.stats

/**
 * An interface for measure amplitude of actual p2p connection
 */
interface KmeSoundAmplitudeListener {

    /**
     * Fired once sound amplitude measured
     *
     * @param amplitude [Int] representation of measured value
     */
    fun onAmplitudeMeasured(amplitude: Int)

}
