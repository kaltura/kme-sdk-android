---
title: IKmeAudioController - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeAudioController](./index.html)

# IKmeAudioController

`interface IKmeAudioController`

An interface for handling audio in the room

### Functions

| [getAvailableAudioDevices](get-available-audio-devices.html) | `abstract fun getAvailableAudioDevices(): `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`KmeAudioDevice`](../../com.kme.kaltura.kmesdk.webrtc.audio/-kme-audio-device/index.html)`?>`<br>Getting set of available audio devices |
| [getSelectedAudioDevice](get-selected-audio-device.html) | `abstract fun getSelectedAudioDevice(): `[`KmeAudioDevice`](../../com.kme.kaltura.kmesdk.webrtc.audio/-kme-audio-device/index.html)`?`<br>Getting last selected audio device |
| [setAudioDevice](set-audio-device.html) | `abstract fun setAudioDevice(device: `[`KmeAudioDevice`](../../com.kme.kaltura.kmesdk.webrtc.audio/-kme-audio-device/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Change current audio device |
| [setDefaultAudioDevice](set-default-audio-device.html) | `abstract fun setDefaultAudioDevice(device: `[`KmeAudioDevice`](../../com.kme.kaltura.kmesdk.webrtc.audio/-kme-audio-device/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Setting default audio device |
| [setListener](set-listener.html) | `abstract fun setListener(listener: `[`AudioManagerListener`](../../com.kme.kaltura.kmesdk.webrtc.audio/-audio-manager-listener/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Listener for detecting audio route changes |
| [start](start.html) | `abstract fun start(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Starting audio manager |
| [stop](stop.html) | `abstract fun stop(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Stopping use audio |

### Inheritors

| [KmeAudioControllerImpl](../../com.kme.kaltura.kmesdk.controller.impl/-kme-audio-controller-impl/index.html) | `class KmeAudioControllerImpl : `[`KmeController`](../../com.kme.kaltura.kmesdk.controller.impl/-kme-controller/index.html)`, `[`IKmeAudioController`](./index.html)<br>An implementation for handling audio in the room |

