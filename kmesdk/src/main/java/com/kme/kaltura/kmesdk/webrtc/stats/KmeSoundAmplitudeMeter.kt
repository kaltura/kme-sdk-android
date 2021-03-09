package com.kme.kaltura.kmesdk.webrtc.stats

import android.os.Handler
import android.os.Looper
import com.kme.kaltura.kmesdk.webrtc.peerconnection.impl.KmePeerConnectionImpl
import org.webrtc.PeerConnection

/**
 * An implementation for measure amplitude of actual p2p connection
 */
class KmeSoundAmplitudeMeter(
    private val peerConnection: PeerConnection,
    private var soundAmplitudeListener: KmeSoundAmplitudeListener?
) {

    private var meterHandler = Handler(Looper.getMainLooper())
    private val soundMeasureRunnable = Runnable { measureAmplitude() }

    /**
     * Start listen for measuring
     */
    fun startMeasure() {
        meterHandler.post(soundMeasureRunnable)
    }

    /**
     * Stop listen for measuring
     */
    fun stopMeasure() {
        meterHandler.removeCallbacks(soundMeasureRunnable)
        soundAmplitudeListener?.onAmplitudeMeasured(0)
    }

    /**
     * Measure sound amplitude based on RTC statistics
     */
    private fun measureAmplitude() {
        peerConnection.getStats {
            it.statsMap?.forEach { statObject ->
                if (statObject.value.type == STATISTICS_MEDIA_SOURCE_KEY &&
                    statObject.value.members.containsKey(STATISTICS_TRACK_ID_KEY) &&
                    statObject.value.members.containsKey(STATISTICS_AUDIO_LEVEL_KEY) &&
                    statObject.value.members.getValue(STATISTICS_TRACK_ID_KEY) == KmePeerConnectionImpl.AUDIO_TRACK_ID
                ) {
                    val value = statObject.value.members.getValue(STATISTICS_AUDIO_LEVEL_KEY)
                    val amplitude = "%.3f".format(value).replace(",", ".").toDouble()
                    soundAmplitudeListener?.onAmplitudeMeasured((amplitude * 1000).toInt())
                    return@getStats
                }
            }
        }
        meterHandler.postDelayed(soundMeasureRunnable, SOUND_METER_DELAY)
    }

    companion object {
        private const val SOUND_METER_DELAY: Long = 500
        private const val STATISTICS_MEDIA_SOURCE_KEY = "media-source"
        private const val STATISTICS_TRACK_ID_KEY = "trackIdentifier"
        private const val STATISTICS_AUDIO_LEVEL_KEY = "audioLevel"
    }

}
