---
title: KmeRoomNotesApiService.deleteRoomNote - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeRoomNotesApiService](index.html) / [deleteRoomNote](./delete-room-note.html)

# deleteRoomNote

`@FormUrlEncoded @POST("note/delete") abstract suspend fun deleteRoomNote(@Field("room_id") roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, @Field("note_id") noteId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`KmeDeleteRoomNoteResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-delete-room-note-response/index.html)

Delete specific note

### Parameters

`roomId` - id of a room

`noteId` - id of a note

**Return**
[KmeDeleteRoomNoteResponse](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-delete-room-note-response/index.html) object in success case

