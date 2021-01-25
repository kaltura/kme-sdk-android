---
title: KmeRoomApiService.getWebRTCLiveServer - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeRoomApiService](index.html) / [getWebRTCLiveServer](./get-web-r-t-c-live-server.html)

# getWebRTCLiveServer

`@GET("room/getWebrtcLiveServer") abstract suspend fun getWebRTCLiveServer(@Query("room_alias") roomAlias: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Query("device_type") deviceType: `[`KmePlatformType`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-platform-type/index.html)` = KmePlatformType.MOBILE): `[`KmeGetWebRTCServerResponse`](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-get-web-r-t-c-server-response/index.html)

Getting data for p2p connection

### Parameters

`roomAlias` - alias of a room

`deviceType` - device type flag

**Return**
[KmeGetWebRTCServerResponse](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-get-web-r-t-c-server-response/index.html) object in success case

