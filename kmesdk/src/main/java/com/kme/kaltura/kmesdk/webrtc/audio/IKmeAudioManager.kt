package com.kme.kaltura.kmesdk.webrtc.audio

/**
 * An interface for handling audio devices in the room
 */
interface IKmeAudioManager {

    /**
     * Starting audio manager
     */
    fun start()

    /**
     * Setting default audio device
     *
     * @param device audio device to use as default
     */
    fun setDefaultAudioDevice(device: KmeAudioDevice)

    /**
     * Change current audio device
     *
     * @param device audio device to use
     */
    fun setAudioDevice(device: KmeAudioDevice)

    /**
     * Detect all available devices and auto switch to most important for now
     */
    fun updateAudioDeviceState()

    /**
     * Getting set of available audio devices
     *
     * @return set of [KmeAudioDevice]
     */
    fun getAvailableAudioDevices(): List<KmeAudioDevice>

    /**
     * Getting last selected audio device
     *
     * @return last selected device as [KmeAudioDevice] object
     */
    fun getSelectedAudioDevice(): KmeAudioDevice?

    /**
     * Listener for detecting audio route changes
     *
     * @param listener
     */
    fun setListener(listener: AudioManagerListener)

    /**
     * Stopping use audio
     */
    fun stop()

}
