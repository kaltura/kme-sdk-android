---
title: IKmeRoomNotesController.renameRoomNote - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeRoomNotesController](index.html) / [renameRoomNote](./rename-room-note.html)

# renameRoomNote

`abstract fun renameRoomNote(roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, noteId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, success: (response: `[`KmeRoomNoteRenameResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-rename-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Renames specific note

### Parameters

`roomId` - id of a room

`noteId` - id of a note

`name` - new name for the note

`success` - function to handle success result. Contains [KmeRoomNoteRenameResponse](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-rename-response/index.html) object

`error` - function to handle error result. Contains [KmeApiException](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html) object