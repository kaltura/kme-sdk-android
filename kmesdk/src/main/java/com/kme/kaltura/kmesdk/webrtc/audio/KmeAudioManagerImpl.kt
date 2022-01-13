package com.kme.kaltura.kmesdk.webrtc.audio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.util.Log
import java.util.*

/**
 * An implementation for handling audio devices in the room
 */
class KmeAudioManagerImpl(
    private val context: Context
) : IKmeAudioManager {
    
    enum class AudioManagerState { UNINITIALIZED, PREINITIALIZED, RUNNING }

    private var audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var listener: AudioManagerListener? = null
    private var state: AudioManagerState? = null
    private var savedAudioMode: Int = AudioManager.MODE_INVALID
    private var savedIsSpeakerPhoneOn = false

    private var hasWiredHeadset = false

    private var defaultAudioDevice = KmeAudioDevice.EARPIECE
    private var selectedAudioDevice = KmeAudioDevice.NONE

    private var userSelectedAudioDevice: KmeAudioDevice? = null

    private var bluetoothManager: KmeBluetoothManager
    private var audioDevices: MutableList<KmeAudioDevice> = mutableListOf()

    private var wiredHeadsetReceiver: BroadcastReceiver? = null
    private var audioFocusChangeListener: OnAudioFocusChangeListener? = null

    init {
        bluetoothManager = KmeBluetoothManager(context, this, audioManager)
        wiredHeadsetReceiver = WiredHeadsetReceiver()
        state = AudioManagerState.UNINITIALIZED
    }

    /**
     * Starting audio manager
     */
    override fun start() {
        if (state == AudioManagerState.RUNNING) {
            return
        }

        state = AudioManagerState.RUNNING

        audioManager.let {
            savedAudioMode = it.mode
            savedIsSpeakerPhoneOn = it.isSpeakerphoneOn
        }
        hasWiredHeadset = hasWiredHeadset()

        Log.e("TAG", "start: savedAudioMode = $savedAudioMode, hasWiredHeadset = $hasWiredHeadset")

//        audioManager.requestAudioFocus(
//            audioFocusChangeListener,
//            AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
//        )

//        val result = audioManager.requestAudioFocus(
//            audioFocusChangeListener,
//            AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
//        )
//        Log.e("TAG", "start: requestAudioFocus = $result", )

        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION

        userSelectedAudioDevice = KmeAudioDevice.NONE
        selectedAudioDevice = KmeAudioDevice.NONE
        audioDevices.clear()

        bluetoothManager.start()

        updateAudioDeviceState()
        registerReceiver(wiredHeadsetReceiver, IntentFilter(Intent.ACTION_HEADSET_PLUG))
    }


    /**
     * Stopping use audio
     */
    override fun stop() {
        if (state != AudioManagerState.RUNNING) {
            return
        }

        state = AudioManagerState.UNINITIALIZED
        unregisterReceiver(wiredHeadsetReceiver)
        bluetoothManager.stop()

        setSpeakerphoneOn(savedIsSpeakerPhoneOn)
        audioManager.mode = savedAudioMode

//        audioManager.abandonAudioFocus(audioFocusChangeListener)
        audioFocusChangeListener = null
        removeListener()
    }

    /**
     * Listener for detecting audio route changes
     */
    override fun setListener(listener: AudioManagerListener) {
        this.listener = listener
    }

    /**
     * Remove listener for detecting audio route changes
     */
    override fun removeListener() {
        this.listener = null
    }

    /**
     * Increase music & voice audio volume
     */
    override fun adjustStreamVolumeRise() {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND)
        audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
    }
    
    /**
     * Decrease music & voice audio volume
     */
    override fun adjustStreamVolumeLow() {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND)
        audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
    }


    /**
     * Setting default audio device
     */
    override fun setDefaultAudioDevice(device: KmeAudioDevice) {
        when (device) {
            KmeAudioDevice.SPEAKER_PHONE -> defaultAudioDevice = device
            KmeAudioDevice.EARPIECE -> defaultAudioDevice = if (hasEarpiece()) {
                device
            } else {
                KmeAudioDevice.SPEAKER_PHONE
            }
            else -> {
            }
        }
        updateAudioDeviceState()
    }

    /**
     * Change current audio device
     *
     * @param device audio device to use
     */
    override fun setAudioDevice(device: KmeAudioDevice) {
        if (audioDevices.contains(device)) {
            userSelectedAudioDevice = device
            updateAudioDeviceState()
        }
    }

    /**
     * Getting set of available audio devices
     */
    override fun getAvailableAudioDevices(): List<KmeAudioDevice> {
        return Collections.unmodifiableList(audioDevices)
    }

    /**
     * Getting last selected audio device
     */
    override fun getSelectedAudioDevice(): KmeAudioDevice {
        return selectedAudioDevice
    }

    private fun setAudioDeviceInternal(device: KmeAudioDevice) {
        when (device) {
            KmeAudioDevice.SPEAKER_PHONE -> setSpeakerphoneOn(true)
            KmeAudioDevice.EARPIECE -> setSpeakerphoneOn(false)
            KmeAudioDevice.WIRED_HEADSET -> setSpeakerphoneOn(false)
            KmeAudioDevice.BLUETOOTH -> setSpeakerphoneOn(false)
            else -> {
            }
        }
        selectedAudioDevice = device
    }

    private fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter) {
        context.registerReceiver(receiver, filter)
    }

    private fun unregisterReceiver(receiver: BroadcastReceiver?) {
        context.unregisterReceiver(receiver)
    }

    /**
     * Toggle for speaker
     */
    private fun setSpeakerphoneOn(on: Boolean) {
        if (audioManager.isSpeakerphoneOn != on) {
            audioManager.isSpeakerphoneOn = on
        }
    }

    /**
     * Checks if phone has earpiece
     */
    private fun hasEarpiece(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
    }

    /**
     * Checks if wired headset is connected
     */
    private fun hasWiredHeadset(): Boolean {
        return audioManager.isWiredHeadsetOn
    }

    /**
     * Detect all available devices and auto switch to most important for now
     */
    override fun updateAudioDeviceState() {
        if (bluetoothManager.getState() === KmeBluetoothManager.State.HEADSET_AVAILABLE ||
            bluetoothManager.getState() === KmeBluetoothManager.State.HEADSET_UNAVAILABLE ||
            bluetoothManager.getState() === KmeBluetoothManager.State.SCO_DISCONNECTING
        ) {
            bluetoothManager.updateDevice()
        }

        val newAudioDevices: MutableList<KmeAudioDevice> = mutableListOf()
        if (bluetoothManager.getState() === KmeBluetoothManager.State.SCO_CONNECTED ||
            bluetoothManager.getState() === KmeBluetoothManager.State.SCO_CONNECTING ||
            bluetoothManager.getState() === KmeBluetoothManager.State.HEADSET_AVAILABLE
        ) {
            newAudioDevices.add(KmeAudioDevice.BLUETOOTH)
        } else {
            if (hasWiredHeadset) {
                newAudioDevices.add(KmeAudioDevice.WIRED_HEADSET)
            } else if (hasEarpiece()) {
                newAudioDevices.add(KmeAudioDevice.EARPIECE)
            }
        }
        newAudioDevices.add(KmeAudioDevice.SPEAKER_PHONE)

        var audioDeviceSetUpdated = audioDevices != newAudioDevices

        audioDevices = newAudioDevices
        if (bluetoothManager.getState() === KmeBluetoothManager.State.HEADSET_UNAVAILABLE &&
            userSelectedAudioDevice == KmeAudioDevice.BLUETOOTH
        ) {
            userSelectedAudioDevice = KmeAudioDevice.NONE
        }

//        if (hasWiredHeadset && userSelectedAudioDevice == KmeAudioDevice.SPEAKER_PHONE) {
//            userSelectedAudioDevice = KmeAudioDevice.WIRED_HEADSET
//        }
        if (!hasWiredHeadset && userSelectedAudioDevice == KmeAudioDevice.WIRED_HEADSET) {
            userSelectedAudioDevice = KmeAudioDevice.EARPIECE
        }

        val needBluetoothAudioStart =
            (bluetoothManager.getState() === KmeBluetoothManager.State.HEADSET_AVAILABLE &&
                    (userSelectedAudioDevice == KmeAudioDevice.NONE || userSelectedAudioDevice == KmeAudioDevice.BLUETOOTH))

        val needBluetoothAudioStop =
            ((bluetoothManager.getState() === KmeBluetoothManager.State.SCO_CONNECTED || bluetoothManager.getState() === KmeBluetoothManager.State.SCO_CONNECTING) &&
                    (userSelectedAudioDevice != KmeAudioDevice.NONE && userSelectedAudioDevice != KmeAudioDevice.BLUETOOTH))

        if (needBluetoothAudioStop) {
            bluetoothManager.stopScoAudio()
            bluetoothManager.updateDevice()
        }
        if (needBluetoothAudioStart && !needBluetoothAudioStop) {
            if (bluetoothManager.startScoAudio()) {
                audioDevices.remove(KmeAudioDevice.BLUETOOTH)
                audioDeviceSetUpdated = true
            }
        }

        val newAudioDevice = when {
            bluetoothManager.getState() === KmeBluetoothManager.State.SCO_CONNECTED -> KmeAudioDevice.BLUETOOTH
//            hasWiredHeadset -> KmeAudioDevice.WIRED_HEADSET
            userSelectedAudioDevice == KmeAudioDevice.SPEAKER_PHONE -> KmeAudioDevice.SPEAKER_PHONE
            userSelectedAudioDevice == KmeAudioDevice.EARPIECE -> KmeAudioDevice.EARPIECE
            userSelectedAudioDevice == KmeAudioDevice.WIRED_HEADSET -> KmeAudioDevice.WIRED_HEADSET
            else -> defaultAudioDevice
        }
        if (newAudioDevice != selectedAudioDevice || audioDeviceSetUpdated) {
            setAudioDeviceInternal(newAudioDevice)
            listener?.onAudioDeviceChanged(selectedAudioDevice, audioDevices)
        }
    }

    /**
     * Callbacks from the system about plugging in and out for wired headset devices
     */
    inner class WiredHeadsetReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra("state", 0)
            val microphone = intent.getIntExtra("microphone", 0)
            val name = intent.getStringExtra("name")

            hasWiredHeadset = state == 1

            userSelectedAudioDevice = if (hasWiredHeadset) {
                KmeAudioDevice.WIRED_HEADSET
            } else {
                KmeAudioDevice.EARPIECE
            }

            updateAudioDeviceState()
        }
    }

}
