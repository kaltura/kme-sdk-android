package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.webrtc.audio.AudioManagerListener
import com.kme.kaltura.kmesdk.webrtc.audio.KmeAudioDevice

interface IKmeAudioController {

    fun start()

    fun setDefaultAudioDevice(device: KmeAudioDevice)

    fun setAudioDevice(device: KmeAudioDevice)

    fun getAvailableAudioDevices(): Set<KmeAudioDevice?>

    fun getSelectedAudioDevice(): KmeAudioDevice?

    fun setListener(listener: AudioManagerListener)

    fun stop()

}
