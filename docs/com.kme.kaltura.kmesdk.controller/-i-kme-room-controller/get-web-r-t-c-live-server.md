---
title: IKmeRoomController.getWebRTCLiveServer - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeRoomController](index.html) / [getWebRTCLiveServer](./get-web-r-t-c-live-server.html)

# getWebRTCLiveServer

`abstract fun getWebRTCLiveServer(roomAlias: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, success: (response: `[`KmeGetWebRTCServerResponse`](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-get-web-r-t-c-server-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Getting data for p2p connection

### Parameters

`roomAlias` - alias of a room

`success` - function to handle success result. Contains [KmeGetWebRTCServerResponse](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-get-web-r-t-c-server-response/index.html) object

`error` - function to handle error result. Contains [KmeApiException](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html) object