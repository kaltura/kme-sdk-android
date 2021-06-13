package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeAudioModule
import com.kme.kaltura.kmesdk.webrtc.audio.AudioManagerListener
import com.kme.kaltura.kmesdk.webrtc.audio.IKmeAudioManager
import com.kme.kaltura.kmesdk.webrtc.audio.KmeAudioDevice
import org.koin.core.inject

/**
 * An implementation for handling audio in the room
 */
class KmeAudioModuleImpl : KmeController(), IKmeAudioModule {

    private val audioManager: IKmeAudioManager by inject()

    /**
     * Starting audio manager
     */
    override fun start() {
        audioManager.start()
    }

    /**
     * Setting default audio device
     */
    override fun setDefaultAudioDevice(device: KmeAudioDevice) {
        audioManager.setDefaultAudioDevice(device)
    }

    /**
     * Change current audio device
     */
    override fun setAudioDevice(device: KmeAudioDevice) {
        audioManager.setAudioDevice(device)
    }

    /**
     * Getting set of available audio devices
     */
    override fun getAvailableAudioDevices(): List<KmeAudioDevice> {
        return audioManager.getAvailableAudioDevices()
    }

    /**
     * Getting last selected audio device
     */
    override fun getSelectedAudioDevice(): KmeAudioDevice? {
        return audioManager.getSelectedAudioDevice()
    }

    /**
     * Listener for detecting audio route changes
     */
    override fun setListener(listener: AudioManagerListener) {
        audioManager.setListener(listener)
    }

    /**
     * Remove listener for detecting audio route changes
     */
    override fun removeListener() {
        audioManager.removeListener()
    }

    /**
     * Stopping use audio
     */
    override fun stop() {
        audioManager.stop()
    }

}
