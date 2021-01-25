---
title: KmeRoomNotesApiService.getDownloadRoomNoteUrl - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeRoomNotesApiService](index.html) / [getDownloadRoomNoteUrl](./get-download-room-note-url.html)

# getDownloadRoomNoteUrl

`@FormUrlEncoded @POST("note/downloadNote") abstract suspend fun getDownloadRoomNoteUrl(@Field("room_id") roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, @Field("note_id") noteId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, @Field("saveToFiles") saveToFiles: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`KmeRoomNoteDownloadUrlResponse`](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-download-url-response/index.html)

Getting an url for download note as pdf file

### Parameters

`roomId` - id of a room

`noteId` - id of a note

`saveToFiles` - save to room files folder

**Return**
[KmeRoomNoteDownloadUrlResponse](../../com.kme.kaltura.kmesdk.rest.response.room.notes/-kme-room-note-download-url-response/index.html) object in success case

