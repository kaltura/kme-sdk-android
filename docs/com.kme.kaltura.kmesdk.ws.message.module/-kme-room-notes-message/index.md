---
title: KmeRoomNotesMessage - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.ws.message.module](../index.html) / [KmeRoomNotesMessage](./index.html)

# KmeRoomNotesMessage

`class KmeRoomNotesMessage<T : `[`NotesPayload`](-notes-payload/index.html)`> : `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<`[`T`](index.html#T)`>`

### Types

| [CreateNotePayload](-create-note-payload/index.html) | `data class CreateNotePayload : `[`NotesPayload`](-notes-payload/index.html) |
| [NewNote](-new-note/index.html) | `data class NewNote` |
| [NewNoteWrapper](-new-note-wrapper/index.html) | `data class NewNoteWrapper` |
| [NoteBlocks](-note-blocks/index.html) | `data class NoteBlocks` |
| [NoteEditBlock](-note-edit-block/index.html) | `data class NoteEditBlock` |
| [NoteEditDelta](-note-edit-delta/index.html) | `data class NoteEditDelta` |
| [NoteEditKeyValueContent](-note-edit-key-value-content/index.html) | `data class NoteEditKeyValueContent` |
| [NoteEditor](-note-editor/index.html) | `data class NoteEditor` |
| [NoteEventData](-note-event-data/index.html) | `data class NoteEventData` |
| [NoteInlineStyle](-note-inline-style/index.html) | `data class NoteInlineStyle` |
| [NotePayload](-note-payload/index.html) | `data class NotePayload : `[`NotesPayload`](-notes-payload/index.html) |
| [NotesPayload](-notes-payload/index.html) | `class NotesPayload : `[`Payload`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/-payload/index.html) |

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeRoomNotesMessage()` |

### Inherited Properties

| [constraint](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/constraint.html) | `var constraint: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KmeConstraint`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-constraint/index.html)`>?` |
| [module](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/module.html) | `var module: `[`KmeMessageModule`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-module/index.html)`?` |
| [name](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/name.html) | `var name: `[`KmeMessageEvent`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-event/index.html)`?` |
| [payload](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/payload.html) | `var payload: `[`T`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html#T)`?` |
| [type](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/type.html) | `var type: `[`KmeMessageEventType`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-event-type/index.html)`?` |

### Extension Functions

| [toType](../../com.kme.kaltura.kmesdk/to-type.html) | `fun <T> `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<*>.toType(): `[`T`](../../com.kme.kaltura.kmesdk/to-type.html#T)`?` |

