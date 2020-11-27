package com.kme.kaltura.kmesdk.webrtc.stats

interface KmeSoundAmplitudeListener {

    fun onAmplitudeMeasured(bringToFront: Boolean, amplitude: Double)

}
