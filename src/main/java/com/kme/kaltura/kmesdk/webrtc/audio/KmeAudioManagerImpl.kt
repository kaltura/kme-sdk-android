package com.kme.kaltura.kmesdk.webrtc.audio

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager

class KmeAudioManagerImpl(context: Context) : IKmeAudioManager {

    private var audioManager: AudioManager? = null
    private var initialValue: Boolean = false

    init {
        audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager?.isSpeakerphoneOn?.let {
            initialValue = it
        }
    }

    override fun enableSpeakerphone(isEnable: Boolean) {
        val wasEnabled = audioManager?.isSpeakerphoneOn
        if (wasEnabled != isEnable) {
            audioManager?.isSpeakerphoneOn = isEnable
        }
        audioManager?.mode = AudioManager.MODE_IN_COMMUNICATION
    }

    override fun close() {
        audioManager?.isSpeakerphoneOn = initialValue
        audioManager?.mode = AudioManager.MODE_NORMAL
    }

}
