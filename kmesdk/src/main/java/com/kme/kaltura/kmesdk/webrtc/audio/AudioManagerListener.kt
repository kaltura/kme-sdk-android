package com.kme.kaltura.kmesdk.webrtc.audio

/**
 * An interface for handling switching between audio devices in the room
 */
interface AudioManagerListener {

    /**
     * Fired once audio device switch detected
     *
     * @param selectedAudioDevice new audio device
     * @param availableAudioDevices collection of available devices
     */
    fun onAudioDeviceChanged(
        selectedAudioDevice: KmeAudioDevice,
        availableAudioDevices: List<KmeAudioDevice>
    )

}