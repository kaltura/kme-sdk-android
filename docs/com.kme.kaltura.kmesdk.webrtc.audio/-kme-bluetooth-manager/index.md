---
title: KmeBluetoothManager - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.webrtc.audio](../index.html) / [KmeBluetoothManager](./index.html)

# KmeBluetoothManager

`class KmeBluetoothManager`

An implementation for handling bluetooth audio device switches

### Types

| [BluetoothHeadsetBroadcastReceiver](-bluetooth-headset-broadcast-receiver/index.html) | `inner class BluetoothHeadsetBroadcastReceiver : `[`BroadcastReceiver`](https://developer.android.com/reference/android/content/BroadcastReceiver.html)<br>Callbacks from the system about changing [BluetoothHeadset](https://developer.android.com/reference/android/bluetooth/BluetoothHeadset.html) state |
| [BluetoothServiceListener](-bluetooth-service-listener/index.html) | `inner class BluetoothServiceListener : `[`ServiceListener`](https://developer.android.com/reference/android/bluetooth/BluetoothProfile/ServiceListener.html)<br>Callbacks from the system about plugging in and out for bluetooth devices |
| [State](-state/index.html) | `enum class State` |

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeBluetoothManager(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`, kmeAudioManager: `[`IKmeAudioManager`](../-i-kme-audio-manager/index.html)`, audioManager: `[`AudioManager`](https://developer.android.com/reference/android/media/AudioManager.html)`)`<br>An implementation for handling bluetooth audio device switches |

### Functions

| [getState](get-state.html) | `fun getState(): `[`State`](-state/index.html)`?` |
| [start](start.html) | `fun start(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Start listen for bluetooth devices |
| [startScoAudio](start-sco-audio.html) | `fun startScoAudio(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Start SCO |
| [stop](stop.html) | `fun stop(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Stop listen for bluetooth devices |
| [stopScoAudio](stop-sco-audio.html) | `fun stopScoAudio(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Stop SCO |
| [updateDevice](update-device.html) | `fun updateDevice(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Update available bluetooth devices |

