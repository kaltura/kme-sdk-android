---
title: IKmeRoomNoteDownloadController.downloadRoomNote - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeRoomNoteDownloadController](index.html) / [downloadRoomNote](./download-room-note.html)

# downloadRoomNote

`abstract fun downloadRoomNote(name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, url: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, success: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`Exception`](https://developer.android.com/reference/java/lang/Exception.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Download specific note as pdf file

### Parameters

`name` - name of a note

`url` - url location of a note

`success` - function to handle success result. Contains [KmeGetRoomNotesResponse](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-get-room-notes-response/index.html) object

`error` - function to handle error result. Contains [KmeApiException](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html) object