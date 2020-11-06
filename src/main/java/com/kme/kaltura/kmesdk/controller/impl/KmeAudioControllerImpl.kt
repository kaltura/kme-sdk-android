package com.kme.kaltura.kmesdk.controller.impl

import com.kme.kaltura.kmesdk.controller.IKmeAudioController
import com.kme.kaltura.kmesdk.webrtc.audio.AudioManagerListener
import com.kme.kaltura.kmesdk.webrtc.audio.IKmeAudioManager
import com.kme.kaltura.kmesdk.webrtc.audio.KmeAudioDevice
import org.koin.core.inject

class KmeAudioControllerImpl : KmeController(), IKmeAudioController {

    private val audioManager: IKmeAudioManager by inject()

    override fun start() {
        audioManager.start()
    }

    override fun setDefaultAudioDevice(device: KmeAudioDevice) {
        audioManager.setDefaultAudioDevice(device)
    }

    override fun setAudioDevice(device: KmeAudioDevice) {
        audioManager.setAudioDevice(device)
    }

    override fun getAvailableAudioDevices(): Set<KmeAudioDevice?> {
        return audioManager.getAvailableAudioDevices()
    }

    override fun getSelectedAudioDevice(): KmeAudioDevice? {
        return audioManager.getSelectedAudioDevice()
    }

    override fun setListener(listener: AudioManagerListener) {
        audioManager.setListener(listener)
    }

    override fun stop() {
        audioManager.stop()
    }

}
