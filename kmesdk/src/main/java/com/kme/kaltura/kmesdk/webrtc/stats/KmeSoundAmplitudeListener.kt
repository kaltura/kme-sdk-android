package com.kme.kaltura.kmesdk.webrtc.stats

/**
 * An interface for measure amplitude of actual p2p connection
 */
interface KmeSoundAmplitudeListener {

    /**
     * Fired once sound amplitude measured
     *
     * @param bringToFront indicates is user currently speaking
     * @param amplitude [Double] representation of measured value
     */
    fun onAmplitudeMeasured(bringToFront: Boolean, amplitude: Double)

}
