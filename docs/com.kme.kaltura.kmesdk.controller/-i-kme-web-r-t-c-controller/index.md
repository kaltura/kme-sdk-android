---
title: IKmeWebRTCController - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeWebRTCController](./index.html)

# IKmeWebRTCController

`interface IKmeWebRTCController`

An interface for wrap actions with [IKmePeerConnectionController](../-i-kme-peer-connection-controller/index.html)

### Functions

| [addPublisherPeerConnection](add-publisher-peer-connection.html) | `abstract fun addPublisherPeerConnection(requestedUserIdStream: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, renderer: `[`KmeSurfaceRendererView`](../../com.kme.kaltura.kmesdk.webrtc.view/-kme-surface-renderer-view/index.html)`, listener: `[`IKmePeerConnectionClientEvents`](../../com.kme.kaltura.kmesdk.webrtc.peerconnection/-i-kme-peer-connection-client-events/index.html)`): `[`IKmePeerConnectionController`](../-i-kme-peer-connection-controller/index.html)`?`<br>Creates publisher connection |
| [addViewerPeerConnection](add-viewer-peer-connection.html) | `abstract fun addViewerPeerConnection(requestedUserIdStream: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, renderer: `[`KmeSurfaceRendererView`](../../com.kme.kaltura.kmesdk.webrtc.view/-kme-surface-renderer-view/index.html)`, listener: `[`IKmePeerConnectionClientEvents`](../../com.kme.kaltura.kmesdk.webrtc.peerconnection/-i-kme-peer-connection-client-events/index.html)`): `[`IKmePeerConnectionController`](../-i-kme-peer-connection-controller/index.html)`?`<br>Creates a viewer connection |
| [disconnectAllConnections](disconnect-all-connections.html) | `abstract fun disconnectAllConnections(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Disconnect all publisher/viewers connections |
| [disconnectPeerConnection](disconnect-peer-connection.html) | `abstract fun disconnectPeerConnection(requestedUserIdStream: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Disconnect publisher/viewer connection by id |
| [getPeerConnection](get-peer-connection.html) | `abstract fun getPeerConnection(requestedUserIdStream: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`IKmePeerConnectionController`](../-i-kme-peer-connection-controller/index.html)`?`<br>Getting publisher/viewer connection by id |
| [getPublisherConnection](get-publisher-connection.html) | `abstract fun getPublisherConnection(): `[`IKmePeerConnectionController`](../-i-kme-peer-connection-controller/index.html)`?`<br>Getting publisher connection if exist |
| [setTurnServer](set-turn-server.html) | `abstract fun setTurnServer(turnUrl: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, turnUser: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, turnCred: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Setting TURN server for RTC |

### Inheritors

| [IKmeRoomController](../-i-kme-room-controller/index.html) | `interface IKmeRoomController : `[`IKmeWebSocketController`](../-i-kme-web-socket-controller/index.html)`, `[`IKmeWebRTCController`](./index.html)<br>An interface for room data |
| [KmeRoomService](../../com.kme.kaltura.kmesdk.service/-kme-room-service/index.html) | `class KmeRoomService : `[`Service`](https://developer.android.com/reference/android/app/Service.html)`, KmeKoinComponent, `[`IKmeWebSocketController`](../-i-kme-web-socket-controller/index.html)`, `[`IKmeWebRTCController`](./index.html)<br>Service wrapper under the room actions |

