package com.kme.kaltura.kmesdk.webrtc.stats

import com.kme.kaltura.kmesdk.webrtc.peerconnection.impl.KmePeerConnectionImpl
import kotlinx.coroutines.*
import org.webrtc.PeerConnection

/**
 * An implementation for measure amplitude of actual p2p connection
 */
class KmeSoundAmplitudeMeter(
    private val peerConnection: PeerConnection,
    private var soundAmplitudeListener: KmeSoundAmplitudeListener?
) {

    private var measureJob: Job? = null

    /**
     * Start listen for measuring
     */
    fun startMeasure() {
        measureJob?.isActive?.let {
            return
        }

        measureJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                measureAmplitude()
                delay(SOUND_METER_DELAY)
            }
        }
    }

    /**
     * Stop listen for measuring
     */
    fun stopMeasure() {
        measureJob?.let {
            if (it.isActive) {
                measureJob?.cancel()
                measureJob = null
            }
        }
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
                    CoroutineScope(Dispatchers.Main).launch {
                        soundAmplitudeListener?.onAmplitudeMeasured((amplitude * 1000).toInt())
                    }
                    return@getStats
                }
            }
        }
    }

    companion object {
        private const val SOUND_METER_DELAY: Long = 250
        private const val STATISTICS_MEDIA_SOURCE_KEY = "media-source"
        private const val STATISTICS_TRACK_ID_KEY = "trackIdentifier"
        private const val STATISTICS_AUDIO_LEVEL_KEY = "audioLevel"
    }

}
