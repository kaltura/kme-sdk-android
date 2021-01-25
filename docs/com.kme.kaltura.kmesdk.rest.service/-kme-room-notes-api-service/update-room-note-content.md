---
title: KmeRoomNotesApiService.updateRoomNoteContent - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeRoomNotesApiService](index.html) / [updateRoomNoteContent](./update-room-note-content.html)

# updateRoomNoteContent

`@FormUrlEncoded @POST("note/updateContent") abstract suspend fun updateRoomNoteContent(@Field("room_id") roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, @Field("note_id") noteId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Field("content") content: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Field("should_update_change_log") updateLogs: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, @Field("html") html: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`KmeRoomNoteUpdateContentResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-update-content-response/index.html)

Changes content in the note

### Parameters

`roomId` - id of a room

`noteId` - id of a note

`content` -

`updateLogs` -

`html` -

**Return**
[KmeRoomNoteUpdateContentResponse](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-update-content-response/index.html) object in success case

