---
title: IKmeRoomNotesController.deleteRoomNote - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeRoomNotesController](index.html) / [deleteRoomNote](./delete-room-note.html)

# deleteRoomNote

`abstract fun deleteRoomNote(roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, noteId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, success: (response: `[`KmeDeleteRoomNoteResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-delete-room-note-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Delete specific note

### Parameters

`roomId` - id of a room

`noteId` - id of a note

`success` - function to handle success result. Contains [KmeDeleteRoomNoteResponse](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-delete-room-note-response/index.html) object

`error` - function to handle error result. Contains [KmeApiException](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html) object