---
title: IKmeRoomNotesController.getRoomNotes - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeRoomNotesController](index.html) / [getRoomNotes](./get-room-notes.html)

# getRoomNotes

`abstract fun getRoomNotes(companyId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, success: (response: `[`KmeGetRoomNotesResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-get-room-notes-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Getting all notes for specific room

### Parameters

`companyId` - id of a company

`roomId` - id of a room

`success` - function to handle success result. Contains [KmeGetRoomNotesResponse](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-get-room-notes-response/index.html) object

`error` - function to handle error result. Contains [KmeApiException](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html) object