package com.kme.kaltura.kmesdk.webrtc.audio

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothProfile.ServiceListener
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Handler
import android.os.Looper

/**
 * An implementation for handling bluetooth audio device switches
 */
class KmeBluetoothManager(
    private val context: Context,
    private val kmeAudioManager: IKmeAudioManager,
    private val audioManager: AudioManager
) {

    enum class State {
        UNINITIALIZED, ERROR,
        HEADSET_UNAVAILABLE, HEADSET_AVAILABLE,
        SCO_DISCONNECTING, SCO_CONNECTING, SCO_CONNECTED
    }

    private var handler: Handler? = null

    private var scoConnectionAttempts = 0
    private var bluetoothState: State? = null

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothHeadset: BluetoothHeadset? = null
    private var bluetoothDevice: BluetoothDevice? = null

    private var bluetoothServiceListener: ServiceListener? = null
    private var bluetoothHeadsetReceiver: BroadcastReceiver? = null

    private val bluetoothTimeoutRunnable = Runnable { bluetoothTimeout() }

    init {
        bluetoothState = State.UNINITIALIZED
        bluetoothServiceListener = BluetoothServiceListener()
        bluetoothHeadsetReceiver = BluetoothHeadsetBroadcastReceiver()

        handler = Handler(Looper.getMainLooper())
    }

    fun getState(): State? {
        return bluetoothState
    }

    /**
     * Start listen for bluetooth devices
     */
    fun start() {
        if (bluetoothState != State.UNINITIALIZED) {
            return
        }

        bluetoothHeadset = null
        bluetoothDevice = null
        scoConnectionAttempts = 0

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            return
        }
        if (!audioManager.isBluetoothScoAvailableOffCall) {
            return
        }
        if (!getBluetoothProfileProxy(
                context,
                bluetoothServiceListener,
                BluetoothProfile.HEADSET
            )
        ) {
            return
        }

        val bluetoothHeadsetFilter = IntentFilter()
        bluetoothHeadsetFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
        bluetoothHeadsetFilter.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED)
        registerReceiver(bluetoothHeadsetReceiver, bluetoothHeadsetFilter)

        bluetoothState = State.HEADSET_UNAVAILABLE
    }

    /**
     * Stop listen for bluetooth devices
     */
    fun stop() {
        unregisterReceiver(bluetoothHeadsetReceiver)

        if (bluetoothAdapter != null) {
            stopScoAudio()
            if (bluetoothState != State.UNINITIALIZED) {
                cancelTimer()
                if (bluetoothHeadset != null) {
                    bluetoothAdapter!!.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset)
                    bluetoothHeadset = null
                }
                bluetoothAdapter = null
                bluetoothDevice = null
                bluetoothState = State.UNINITIALIZED
            }
        }
    }

    /**
     * Start SCO
     */
    fun startScoAudio(): Boolean {
        if (scoConnectionAttempts >= MAX_SCO_CONNECTION_ATTEMPTS) {
            return false
        }
        if (bluetoothState != State.HEADSET_AVAILABLE) {
            return false
        }

        bluetoothState = State.SCO_CONNECTING
        audioManager.startBluetoothSco()
        scoConnectionAttempts++
        startTimer()
        return true
    }

    /**
     * Stop SCO
     */
    fun stopScoAudio() {
        if (bluetoothState != State.SCO_CONNECTING && bluetoothState != State.SCO_CONNECTED) {
            return
        }

        cancelTimer()
        audioManager.stopBluetoothSco()
        bluetoothState = State.SCO_DISCONNECTING
    }

    /**
     * Update available bluetooth devices
     */
    fun updateDevice() {
        if (bluetoothState == State.UNINITIALIZED || bluetoothHeadset == null) {
            return
        }

        val devices = bluetoothHeadset!!.connectedDevices
        if (devices.isEmpty()) {
            bluetoothDevice = null
            bluetoothState = State.HEADSET_UNAVAILABLE
        } else {
            bluetoothDevice = devices[0]
            bluetoothState = State.HEADSET_AVAILABLE
        }
    }

    private fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?) {
        context.registerReceiver(receiver, filter)
    }

    private fun unregisterReceiver(receiver: BroadcastReceiver?) {
        context.unregisterReceiver(receiver)
    }

    private fun getBluetoothProfileProxy(
        context: Context,
        listener: ServiceListener?,
        profile: Int
    ): Boolean {
        return bluetoothAdapter!!.getProfileProxy(context, listener, profile)
    }

    private fun updateAudioDeviceState() {
        kmeAudioManager.updateAudioDeviceState()
    }

    private fun startTimer() {
        handler?.postDelayed(bluetoothTimeoutRunnable, BLUETOOTH_SCO_TIMEOUT_MS)
    }

    private fun cancelTimer() {
        handler?.removeCallbacks(bluetoothTimeoutRunnable)
    }

    private fun bluetoothTimeout() {
        if (bluetoothState == State.UNINITIALIZED || bluetoothHeadset == null) {
            return
        }
        if (bluetoothState != State.SCO_CONNECTING) {
            return
        }

        var scoConnected = false
        val devices = bluetoothHeadset!!.connectedDevices
        if (devices.size > 0) {
            bluetoothDevice = devices[0]
            if (bluetoothHeadset!!.isAudioConnected(bluetoothDevice)) {
                scoConnected = true
            }
        }
        if (scoConnected) {
            bluetoothState = State.SCO_CONNECTED
            scoConnectionAttempts = 0
        } else {
            stopScoAudio()
        }
        updateAudioDeviceState()
    }

    /**
     * Callbacks from the system about plugging in and out for bluetooth devices
     */
    inner class BluetoothServiceListener : ServiceListener {

        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile != BluetoothProfile.HEADSET || bluetoothState == State.UNINITIALIZED) {
                return
            }

            bluetoothHeadset = proxy as BluetoothHeadset
            updateAudioDeviceState()
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile != BluetoothProfile.HEADSET || bluetoothState == State.UNINITIALIZED) {
                return
            }

            stopScoAudio()
            bluetoothHeadset = null
            bluetoothDevice = null
            bluetoothState = State.HEADSET_UNAVAILABLE
            updateAudioDeviceState()
        }
    }

    /**
     * Callbacks from the system about changing [BluetoothHeadset] state
     */
    inner class BluetoothHeadsetBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            if (bluetoothState == State.UNINITIALIZED) {
                return
            }

            val action = intent.action
            if (action == BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED) {
                val state = intent.getIntExtra(
                    BluetoothHeadset.EXTRA_STATE,
                    BluetoothHeadset.STATE_DISCONNECTED
                )

                when (state) {
                    BluetoothHeadset.STATE_CONNECTED -> {
                        scoConnectionAttempts = 0
                        updateAudioDeviceState()
                        kmeAudioManager.setAudioDevice(KmeAudioDevice.BLUETOOTH)
                    }
                    BluetoothHeadset.STATE_DISCONNECTED -> {
                        stopScoAudio()
                        updateAudioDeviceState()
                    }
                }
            } else if (action == BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED) {
                val state = intent.getIntExtra(
                    BluetoothHeadset.EXTRA_STATE,
                    BluetoothHeadset.STATE_AUDIO_DISCONNECTED
                )

                if (state == BluetoothHeadset.STATE_AUDIO_CONNECTED) {
                    cancelTimer()
                    if (bluetoothState == State.SCO_CONNECTING) {
                        bluetoothState = State.SCO_CONNECTED
                        scoConnectionAttempts = 0
                        updateAudioDeviceState()
                    }
                } else if (state == BluetoothHeadset.STATE_AUDIO_DISCONNECTED) {
                    if (isInitialStickyBroadcast) {
                        return
                    }
                    updateAudioDeviceState()
                }
            }
        }
    }

    companion object {
        private const val BLUETOOTH_SCO_TIMEOUT_MS = 4000L
        private const val MAX_SCO_CONNECTION_ATTEMPTS = 2
    }

}
