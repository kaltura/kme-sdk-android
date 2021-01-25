---
title: KmeRoomApiService.getRooms - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeRoomApiService](index.html) / [getRooms](./get-rooms.html)

# getRooms

`@GET("room/getRoomListForCompany") abstract suspend fun getRooms(@Query("company_id") companyId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, @Query("page_number") pages: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, @Query("limit") limit: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`KmeGetRoomsResponse`](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-get-rooms-response/index.html)

Getting all rooms for specific company

### Parameters

`companyId` - id of a company

`pages` - page number

`limit` - count of rooms per page

**Return**
[KmeGetRoomsResponse](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-get-rooms-response/index.html) object in success case

