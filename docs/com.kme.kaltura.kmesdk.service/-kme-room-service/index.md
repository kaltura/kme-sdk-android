---
title: KmeRoomService - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.service](../index.html) / [KmeRoomService](./index.html)

# KmeRoomService

`class KmeRoomService : `[`Service`](https://developer.android.com/reference/android/app/Service.html)`, KmeKoinComponent, `[`IKmeWebSocketController`](../../com.kme.kaltura.kmesdk.controller/-i-kme-web-socket-controller/index.html)`, `[`IKmeWebRTCController`](../../com.kme.kaltura.kmesdk.controller/-i-kme-web-r-t-c-controller/index.html)

Service wrapper under the room actions

### Types

| [RoomServiceBinder](-room-service-binder/index.html) | `inner class RoomServiceBinder : `[`Binder`](https://developer.android.com/reference/android/os/Binder.html) |

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeRoomService()`<br>Service wrapper under the room actions |

### Functions

| [addPublisherPeerConnection](add-publisher-peer-connection.html) | `fun addPublisherPeerConnection(requestedUserIdStream: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, renderer: `[`KmeSurfaceRendererView`](../../com.kme.kaltura.kmesdk.webrtc.view/-kme-surface-renderer-view/index.html)`, listener: `[`IKmePeerConnectionClientEvents`](../../com.kme.kaltura.kmesdk.webrtc.peerconnection/-i-kme-peer-connection-client-events/index.html)`): `[`IKmePeerConnectionController`](../../com.kme.kaltura.kmesdk.controller/-i-kme-peer-connection-controller/index.html)`?`<br>Creates publisher connection |
| [addViewerPeerConnection](add-viewer-peer-connection.html) | `fun addViewerPeerConnection(requestedUserIdStream: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, renderer: `[`KmeSurfaceRendererView`](../../com.kme.kaltura.kmesdk.webrtc.view/-kme-surface-renderer-view/index.html)`, listener: `[`IKmePeerConnectionClientEvents`](../../com.kme.kaltura.kmesdk.webrtc.peerconnection/-i-kme-peer-connection-client-events/index.html)`): `[`IKmePeerConnectionController`](../../com.kme.kaltura.kmesdk.controller/-i-kme-peer-connection-controller/index.html)`?`<br>Creates a viewer connection |
| [connect](connect.html) | `fun connect(url: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, companyId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, isReconnect: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, token: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, listener: `[`IKmeWSConnectionListener`](../../com.kme.kaltura.kmesdk.ws/-i-kme-w-s-connection-listener/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Establish socket connection |
| [disconnect](disconnect.html) | `fun disconnect(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Disconnect from the room. Destroy all related connections |
| [disconnectAllConnections](disconnect-all-connections.html) | `fun disconnectAllConnections(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Disconnect all publisher/viewers connections |
| [disconnectPeerConnection](disconnect-peer-connection.html) | `fun disconnectPeerConnection(requestedUserIdStream: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Disconnect publisher/viewer connection by id |
| [getPeerConnection](get-peer-connection.html) | `fun getPeerConnection(requestedUserIdStream: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`IKmePeerConnectionController`](../../com.kme.kaltura.kmesdk.controller/-i-kme-peer-connection-controller/index.html)`?`<br>Getting publisher/viewer connection by id |
| [getPublisherConnection](get-publisher-connection.html) | `fun getPublisherConnection(): `[`IKmePeerConnectionController`](../../com.kme.kaltura.kmesdk.controller/-i-kme-peer-connection-controller/index.html)`?`<br>Getting publisher connection if exist |
| [isConnected](is-connected.html) | `fun isConnected(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check is socket connected |
| [onBind](on-bind.html) | `fun onBind(intent: `[`Intent`](https://developer.android.com/reference/android/content/Intent.html)`?): `[`IBinder`](https://developer.android.com/reference/android/os/IBinder.html) |
| [onCreate](on-create.html) | `fun onCreate(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onDestroy](on-destroy.html) | `fun onDestroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onStartCommand](on-start-command.html) | `fun onStartCommand(intent: `[`Intent`](https://developer.android.com/reference/android/content/Intent.html)`?, flags: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, startId: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [onUnbind](on-unbind.html) | `fun onUnbind(intent: `[`Intent`](https://developer.android.com/reference/android/content/Intent.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [send](send.html) | `fun send(message: `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<out `[`Payload`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/-payload/index.html)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Send message via socket |
| [setTurnServer](set-turn-server.html) | `fun setTurnServer(turnUrl: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, turnUser: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, turnCred: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Setting TURN server for RTC |

