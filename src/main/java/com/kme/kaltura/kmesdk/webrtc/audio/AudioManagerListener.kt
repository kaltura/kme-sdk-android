package com.kme.kaltura.kmesdk.webrtc.audio

interface AudioManagerListener {

    fun onAudioDeviceChanged(
        selectedAudioDevice: KmeAudioDevice,
        availableAudioDevices: Set<KmeAudioDevice>
    )

}