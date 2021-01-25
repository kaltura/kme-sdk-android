---
title: KmeRoomApiService.getRoomInfo - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeRoomApiService](index.html) / [getRoomInfo](./get-room-info.html)

# getRoomInfo

`@GET("room/roomInfoByAlias") abstract suspend fun getRoomInfo(@Query("alias") alias: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Query("with_viewed_files") withFiles: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, @Query("check_permission") checkPermission: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`KmeGetRoomInfoResponse`](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-get-room-info-response/index.html)

Getting room info by alias

### Parameters

`alias` - alias of a room

`withFiles` -

`checkPermission` -

**Return**
[KmeGetRoomInfoResponse](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-get-room-info-response/index.html) object in success case

