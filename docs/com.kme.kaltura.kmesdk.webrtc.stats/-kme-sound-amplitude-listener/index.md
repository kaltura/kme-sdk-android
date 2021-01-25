---
title: KmeSoundAmplitudeListener - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.webrtc.stats](../index.html) / [KmeSoundAmplitudeListener](./index.html)

# KmeSoundAmplitudeListener

`interface KmeSoundAmplitudeListener`

An interface for measure amplitude of actual p2p connection

### Functions

| [onAmplitudeMeasured](on-amplitude-measured.html) | `abstract fun onAmplitudeMeasured(bringToFront: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, amplitude: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Fired once sound amplitude measured |

### Inheritors

| [KmePeerConnectionImpl](../../com.kme.kaltura.kmesdk.webrtc.peerconnection.impl/-kme-peer-connection-impl/index.html) | `class KmePeerConnectionImpl : `[`IKmePeerConnection`](../../com.kme.kaltura.kmesdk.webrtc.peerconnection/-i-kme-peer-connection/index.html)`, `[`KmeSoundAmplitudeListener`](./index.html)<br>An implementation actions under WebRTC peer connection object |

