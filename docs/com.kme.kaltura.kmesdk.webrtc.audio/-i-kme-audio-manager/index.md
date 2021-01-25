---
title: IKmeAudioManager - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.webrtc.audio](../index.html) / [IKmeAudioManager](./index.html)

# IKmeAudioManager

`interface IKmeAudioManager`

An interface for handling audio devices in the room

### Functions

| [getAvailableAudioDevices](get-available-audio-devices.html) | `abstract fun getAvailableAudioDevices(): `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`KmeAudioDevice`](../-kme-audio-device/index.html)`?>`<br>Getting set of available audio devices |
| [getSelectedAudioDevice](get-selected-audio-device.html) | `abstract fun getSelectedAudioDevice(): `[`KmeAudioDevice`](../-kme-audio-device/index.html)`?`<br>Getting last selected audio device |
| [setAudioDevice](set-audio-device.html) | `abstract fun setAudioDevice(device: `[`KmeAudioDevice`](../-kme-audio-device/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Change current audio device |
| [setDefaultAudioDevice](set-default-audio-device.html) | `abstract fun setDefaultAudioDevice(device: `[`KmeAudioDevice`](../-kme-audio-device/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Setting default audio device |
| [setListener](set-listener.html) | `abstract fun setListener(listener: `[`AudioManagerListener`](../-audio-manager-listener/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Listener for detecting audio route changes |
| [start](start.html) | `abstract fun start(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Starting audio manager |
| [stop](stop.html) | `abstract fun stop(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Stopping use audio |
| [updateAudioDeviceState](update-audio-device-state.html) | `abstract fun updateAudioDeviceState(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Detect all available devices and auto switch to most important for now |

### Inheritors

| [KmeAudioManagerImpl](../-kme-audio-manager-impl/index.html) | `class KmeAudioManagerImpl : `[`IKmeAudioManager`](./index.html)<br>An implementation for handling audio devices in the room |

