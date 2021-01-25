---
title: IKmeWebSocketController - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeWebSocketController](./index.html)

# IKmeWebSocketController

`interface IKmeWebSocketController`

An interface for communication with socket in the room

### Functions

| [connect](connect.html) | `abstract fun connect(url: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, companyId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, isReconnect: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, token: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, listener: `[`IKmeWSConnectionListener`](../../com.kme.kaltura.kmesdk.ws/-i-kme-w-s-connection-listener/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Establish socket connection |
| [disconnect](disconnect.html) | `abstract fun disconnect(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Disconnect socket connection |
| [isConnected](is-connected.html) | `abstract fun isConnected(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check is socket connected |
| [send](send.html) | `abstract fun send(message: `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<out `[`Payload`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/-payload/index.html)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Send message via socket |

### Inheritors

| [IKmeRoomController](../-i-kme-room-controller/index.html) | `interface IKmeRoomController : `[`IKmeWebSocketController`](./index.html)`, `[`IKmeWebRTCController`](../-i-kme-web-r-t-c-controller/index.html)<br>An interface for room data |
| [KmeRoomService](../../com.kme.kaltura.kmesdk.service/-kme-room-service/index.html) | `class KmeRoomService : `[`Service`](https://developer.android.com/reference/android/app/Service.html)`, KmeKoinComponent, `[`IKmeWebSocketController`](./index.html)`, `[`IKmeWebRTCController`](../-i-kme-web-r-t-c-controller/index.html)<br>Service wrapper under the room actions |

