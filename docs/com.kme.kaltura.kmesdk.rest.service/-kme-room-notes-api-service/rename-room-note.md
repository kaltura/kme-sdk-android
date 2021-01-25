---
title: KmeRoomNotesApiService.renameRoomNote - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeRoomNotesApiService](index.html) / [renameRoomNote](./rename-room-note.html)

# renameRoomNote

`@FormUrlEncoded @POST("note/updateNoteName") abstract suspend fun renameRoomNote(@Field("room_id") roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, @Field("note_id") noteId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Field("newName") name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`KmeRoomNoteRenameResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-rename-response/index.html)

Renames specific note

### Parameters

`roomId` - id of a room

`noteId` - id of a note

`name` - new note name

**Return**
[KmeRoomNoteRenameResponse](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-rename-response/index.html) object in success case

