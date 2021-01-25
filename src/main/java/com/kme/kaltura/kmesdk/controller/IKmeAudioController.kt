package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.webrtc.audio.AudioManagerListener
import com.kme.kaltura.kmesdk.webrtc.audio.KmeAudioDevice

/**
 * An interface for handling audio in the room
 */
interface IKmeAudioController {

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
     * Getting set of available audio devices
     *
     * @return set of [KmeAudioDevice]
     */
    fun getAvailableAudioDevices(): Set<KmeAudioDevice?>

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
