---
title: IKmePeerConnectionController - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmePeerConnectionController](./index.html)

# IKmePeerConnectionController

`interface IKmePeerConnectionController`

An interface for actions related to p2p connection

### Functions

| [createOffer](create-offer.html) | `abstract fun createOffer(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Creates an offers |
| [createPeerConnection](create-peer-connection.html) | `abstract fun createPeerConnection(isPublisher: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, requestedUserIdStream: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, listener: `[`IKmePeerConnectionClientEvents`](../../com.kme.kaltura.kmesdk.webrtc.peerconnection/-i-kme-peer-connection-client-events/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Creates p2p connection |
| [disconnectPeerConnection](disconnect-peer-connection.html) | `abstract fun disconnectPeerConnection(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Closes actual p2p connection |
| [enableAudio](enable-audio.html) | `abstract fun enableAudio(isEnable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Toggle audio |
| [enableCamera](enable-camera.html) | `abstract fun enableCamera(isEnable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Toggle camera |
| [setLocalRenderer](set-local-renderer.html) | `abstract fun setLocalRenderer(localRenderer: `[`KmeSurfaceRendererView`](../../com.kme.kaltura.kmesdk.webrtc.view/-kme-surface-renderer-view/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Setting view for local stream rendering |
| [setMediaServerId](set-media-server-id.html) | `abstract fun setMediaServerId(mediaServerId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Setting media server id for data relay |
| [setRemoteRenderer](set-remote-renderer.html) | `abstract fun setRemoteRenderer(remoteRenderer: `[`KmeSurfaceRendererView`](../../com.kme.kaltura.kmesdk.webrtc.view/-kme-surface-renderer-view/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Setting view for remote stream rendering |
| [setRemoteSdp](set-remote-sdp.html) | `abstract fun setRemoteSdp(type: `[`KmeSdpType`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-sdp-type/index.html)`, sdp: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Setting remote SDP |
| [setTurnServer](set-turn-server.html) | `abstract fun setTurnServer(turnUrl: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, turnUser: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, turnCred: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Setting TURN server for RTC |
| [switchCamera](switch-camera.html) | `abstract fun switchCamera(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Switch between existing cameras |

### Inheritors

| [KmePeerConnectionControllerImpl](../../com.kme.kaltura.kmesdk.controller.impl/-kme-peer-connection-controller-impl/index.html) | `class KmePeerConnectionControllerImpl : `[`IKmePeerConnectionController`](./index.html)`, `[`IKmePeerConnectionEvents`](../../com.kme.kaltura.kmesdk.webrtc.peerconnection/-i-kme-peer-connection-events/index.html)<br>An implementation for p2p connection |

