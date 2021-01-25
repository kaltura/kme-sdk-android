---
title: IKmeRoomController.getRooms - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeRoomController](index.html) / [getRooms](./get-rooms.html)

# getRooms

`abstract fun getRooms(companyId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, pages: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, limit: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, success: (response: `[`KmeGetRoomsResponse`](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-get-rooms-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Getting all rooms for specific company

### Parameters

`companyId` - id of a company

`pages` - page number

`limit` - count of rooms per page

`success` - function to handle success result. Contains [KmeGetRoomsResponse](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-get-rooms-response/index.html) object

`error` - function to handle error result. Contains [KmeApiException](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html) object