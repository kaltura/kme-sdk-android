---
title: IKmeRoomNoteDownloadController - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeRoomNoteDownloadController](./index.html)

# IKmeRoomNoteDownloadController

`interface IKmeRoomNoteDownloadController`

An interface for download notes

### Functions

| [downloadRoomNote](download-room-note.html) | `abstract fun downloadRoomNote(name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, url: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, success: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`Exception`](https://developer.android.com/reference/java/lang/Exception.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Download specific note as pdf file |

### Inheritors

| [IKmeRoomNotesController](../-i-kme-room-notes-controller/index.html) | `interface IKmeRoomNotesController : `[`IKmeRoomNoteDownloadController`](./index.html)<br>An interface for actions with notes |

