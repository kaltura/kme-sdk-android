---
title: KmeRoomApiService - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeRoomApiService](./index.html)

# KmeRoomApiService

`interface KmeRoomApiService`

An interface for room data API calls

### Functions

| [getRoomInfo](get-room-info.html) | `abstract suspend fun getRoomInfo(alias: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, withFiles: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, checkPermission: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`KmeGetRoomInfoResponse`](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-get-room-info-response/index.html)<br>Getting room info by alias |
| [getRooms](get-rooms.html) | `abstract suspend fun getRooms(companyId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, pages: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, limit: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`KmeGetRoomsResponse`](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-get-rooms-response/index.html)<br>Getting all rooms for specific company |
| [getWebRTCLiveServer](get-web-r-t-c-live-server.html) | `abstract suspend fun getWebRTCLiveServer(roomAlias: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, deviceType: `[`KmePlatformType`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-platform-type/index.html)` = KmePlatformType.MOBILE): `[`KmeGetWebRTCServerResponse`](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-get-web-r-t-c-server-response/index.html)<br>Getting data for p2p connection |

