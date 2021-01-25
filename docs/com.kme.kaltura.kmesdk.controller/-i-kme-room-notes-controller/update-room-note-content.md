---
title: IKmeRoomNotesController.updateRoomNoteContent - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeRoomNotesController](index.html) / [updateRoomNoteContent](./update-room-note-content.html)

# updateRoomNoteContent

`abstract fun updateRoomNoteContent(roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, noteId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, content: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, updateLogs: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, html: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, success: (response: `[`KmeRoomNoteUpdateContentResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-update-content-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Changes content in the note

### Parameters

`roomId` - id of a room

`noteId` - id of a note

`content` -

`updateLogs` -

`html` -

`success` - function to handle success result. Contains [KmeRoomNoteUpdateContentResponse](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-update-content-response/index.html) object

`error` - function to handle error result. Contains [KmeApiException](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html) object