---
title: KmeRoomNotesApiService.getRoomNotes - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeRoomNotesApiService](index.html) / [getRoomNotes](./get-room-notes.html)

# getRoomNotes

`@GET("note/getRoomNotes") abstract suspend fun getRoomNotes(@Query("company_id") companyId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, @Query("room_id") roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`KmeGetRoomNotesResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-get-room-notes-response/index.html)

Getting all notes for specific room

### Parameters

`companyId` - id of a company

`roomId` - id of a room

**Return**
[KmeGetRoomNotesResponse](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-get-room-notes-response/index.html) object in success case

