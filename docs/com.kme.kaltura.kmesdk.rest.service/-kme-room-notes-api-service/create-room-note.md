---
title: KmeRoomNotesApiService.createRoomNote - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeRoomNotesApiService](index.html) / [createRoomNote](./create-room-note.html)

# createRoomNote

`@FormUrlEncoded @POST("note/create") abstract suspend fun createRoomNote(@Field("company_id") companyId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, @Field("room_id") roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`KmeRoomNoteCreateResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-create-response/index.html)

Creates a new note in the room

### Parameters

`companyId` - id of a company

`roomId` - id of a room

**Return**
[KmeRoomNoteCreateResponse](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-create-response/index.html) object in success case

