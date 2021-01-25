---
title: KmeRoomNotesApiService - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeRoomNotesApiService](./index.html)

# KmeRoomNotesApiService

`interface KmeRoomNotesApiService`

An interface for notes API calls

### Functions

| [createRoomNote](create-room-note.html) | `abstract suspend fun createRoomNote(companyId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`KmeRoomNoteCreateResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-create-response/index.html)<br>Creates a new note in the room |
| [deleteRoomNote](delete-room-note.html) | `abstract suspend fun deleteRoomNote(roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, noteId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`KmeDeleteRoomNoteResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-delete-room-note-response/index.html)<br>Delete specific note |
| [getDownloadRoomNoteUrl](get-download-room-note-url.html) | `abstract suspend fun getDownloadRoomNoteUrl(roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, noteId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, saveToFiles: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`KmeRoomNoteDownloadUrlResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-download-url-response/index.html)<br>Getting an url for download note as pdf file |
| [getRoomNotes](get-room-notes.html) | `abstract suspend fun getRoomNotes(companyId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`KmeGetRoomNotesResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-get-room-notes-response/index.html)<br>Getting all notes for specific room |
| [renameRoomNote](rename-room-note.html) | `abstract suspend fun renameRoomNote(roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, noteId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`KmeRoomNoteRenameResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-rename-response/index.html)<br>Renames specific note |
| [updateRoomNoteContent](update-room-note-content.html) | `abstract suspend fun updateRoomNoteContent(roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, noteId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, content: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, updateLogs: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, html: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`KmeRoomNoteUpdateContentResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-update-content-response/index.html)<br>Changes content in the note |

