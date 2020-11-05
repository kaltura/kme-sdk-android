package com.kme.kaltura.kmesdk.webrtc.audio

interface IKmeAudioManager {

    fun start()

    fun setDefaultAudioDevice(device: KmeAudioDevice)

    fun setAudioDevice(device: KmeAudioDevice)

    fun updateAudioDeviceState()

    fun getAvailableAudioDevices(): Set<KmeAudioDevice?>

    fun getSelectedAudioDevice(): KmeAudioDevice?

    fun setListener(listener: AudioManagerListener)

    fun stop()

}
