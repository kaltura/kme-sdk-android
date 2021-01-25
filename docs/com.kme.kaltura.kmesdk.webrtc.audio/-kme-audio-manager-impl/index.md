---
title: KmeAudioManagerImpl - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.webrtc.audio](../index.html) / [KmeAudioManagerImpl](./index.html)

# KmeAudioManagerImpl

`class KmeAudioManagerImpl : `[`IKmeAudioManager`](../-i-kme-audio-manager/index.html)

An implementation for handling audio devices in the room

### Types

| [AudioManagerState](-audio-manager-state/index.html) | `enum class AudioManagerState` |
| [WiredHeadsetReceiver](-wired-headset-receiver/index.html) | `inner class WiredHeadsetReceiver : `[`BroadcastReceiver`](https://developer.android.com/reference/android/content/BroadcastReceiver.html)<br>Callbacks from the system about plugging in and out for wired headset devices |

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeAudioManagerImpl(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`)`<br>An implementation for handling audio devices in the room |

### Functions

| [getAvailableAudioDevices](get-available-audio-devices.html) | `fun getAvailableAudioDevices(): `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`KmeAudioDevice`](../-kme-audio-device/index.html)`?>`<br>Getting set of available audio devices |
| [getSelectedAudioDevice](get-selected-audio-device.html) | `fun getSelectedAudioDevice(): `[`KmeAudioDevice`](../-kme-audio-device/index.html)<br>Getting last selected audio device |
| [setAudioDevice](set-audio-device.html) | `fun setAudioDevice(device: `[`KmeAudioDevice`](../-kme-audio-device/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Change current audio device |
| [setDefaultAudioDevice](set-default-audio-device.html) | `fun setDefaultAudioDevice(device: `[`KmeAudioDevice`](../-kme-audio-device/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Setting default audio device |
| [setListener](set-listener.html) | `fun setListener(listener: `[`AudioManagerListener`](../-audio-manager-listener/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Listener for detecting audio route changes |
| [start](start.html) | `fun start(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Starting audio manager |
| [stop](stop.html) | `fun stop(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Stopping use audio |
| [updateAudioDeviceState](update-audio-device-state.html) | `fun updateAudioDeviceState(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Detect all available devices and auto switch to most important for now |

