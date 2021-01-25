---
title: KmeMessage - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.ws.message](../index.html) / [KmeMessage](./index.html)

# KmeMessage

`open class KmeMessage<T : `[`Payload`](-payload/index.html)`>`

### Types

| [Payload](-payload/index.html) | `open class Payload` |

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeMessage()` |

### Properties

| [constraint](constraint.html) | `var constraint: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KmeConstraint`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-constraint/index.html)`>?` |
| [module](module.html) | `var module: `[`KmeMessageModule`](../-kme-message-module/index.html)`?` |
| [name](name.html) | `var name: `[`KmeMessageEvent`](../-kme-message-event/index.html)`?` |
| [payload](payload.html) | `var payload: `[`T`](index.html#T)`?` |
| [type](type.html) | `var type: `[`KmeMessageEventType`](../-kme-message-event-type/index.html)`?` |

### Extension Functions

| [toType](../../com.kme.kaltura.kmesdk/to-type.html) | `fun <T> `[`KmeMessage`](./index.html)`<*>.toType(): `[`T`](../../com.kme.kaltura.kmesdk/to-type.html#T)`?` |

### Inheritors

| [KmeActiveContentModuleMessage](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-active-content-module-message/index.html) | `class KmeActiveContentModuleMessage<T : `[`ActiveContentPayload`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-active-content-module-message/-active-content-payload/index.html)`> : `[`KmeMessage`](./index.html)`<`[`T`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-active-content-module-message/index.html#T)`>` |
| [KmeBannersModuleMessage](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-banners-module-message/index.html) | `class KmeBannersModuleMessage<T : `[`BannersPayload`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-banners-module-message/-banners-payload/index.html)`> : `[`KmeMessage`](./index.html)`<`[`T`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-banners-module-message/index.html#T)`>` |
| [KmeChatModuleMessage](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-chat-module-message/index.html) | `class KmeChatModuleMessage<T : `[`ChatPayload`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-chat-module-message/-chat-payload/index.html)`> : `[`KmeMessage`](./index.html)`<`[`T`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-chat-module-message/index.html#T)`>` |
| [KmeDesktopShareModuleMessage](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-desktop-share-module-message/index.html) | `class KmeDesktopShareModuleMessage<T : `[`DesktopSharePayload`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-desktop-share-module-message/-desktop-share-payload/index.html)`> : `[`KmeMessage`](./index.html)`<`[`T`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-desktop-share-module-message/index.html#T)`>` |
| [KmeParticipantsModuleMessage](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-participants-module-message/index.html) | `class KmeParticipantsModuleMessage<T : `[`ParticipantsPayload`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-participants-module-message/-participants-payload/index.html)`> : `[`KmeMessage`](./index.html)`<`[`T`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-participants-module-message/index.html#T)`>` |
| [KmeRoomInitModuleMessage](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-room-init-module-message/index.html) | `class KmeRoomInitModuleMessage<T : `[`RoomInitPayload`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-room-init-module-message/-room-init-payload/index.html)`> : `[`KmeMessage`](./index.html)`<`[`T`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-room-init-module-message/index.html#T)`>` |
| [KmeRoomNotesMessage](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-room-notes-message/index.html) | `class KmeRoomNotesMessage<T : `[`NotesPayload`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-room-notes-message/-notes-payload/index.html)`> : `[`KmeMessage`](./index.html)`<`[`T`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-room-notes-message/index.html#T)`>` |
| [KmeRoomRecordingMessage](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-room-recording-message/index.html) | `class KmeRoomRecordingMessage<T : `[`RecordingPayload`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-room-recording-message/-recording-payload/index.html)`> : `[`KmeMessage`](./index.html)`<`[`T`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-room-recording-message/index.html#T)`>` |
| [KmeRoomSettingsModuleMessage](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-room-settings-module-message/index.html) | `class KmeRoomSettingsModuleMessage<T : `[`SettingsPayload`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-room-settings-module-message/-settings-payload/index.html)`> : `[`KmeMessage`](./index.html)`<`[`T`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-room-settings-module-message/index.html#T)`>` |
| [KmeSlidesPlayerModuleMessage](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-slides-player-module-message/index.html) | `class KmeSlidesPlayerModuleMessage<T : `[`SlidePlayerPayload`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-slides-player-module-message/-slide-player-payload/index.html)`> : `[`KmeMessage`](./index.html)`<`[`T`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-slides-player-module-message/index.html#T)`>` |
| [KmeStreamingModuleMessage](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-streaming-module-message/index.html) | `class KmeStreamingModuleMessage<T : `[`StreamingPayload`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-streaming-module-message/-streaming-payload/index.html)`> : `[`KmeMessage`](./index.html)`<`[`T`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-streaming-module-message/index.html#T)`>` |
| [KmeVideoModuleMessage](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-video-module-message/index.html) | `class KmeVideoModuleMessage<T : `[`VideoPayload`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-video-module-message/-video-payload/index.html)`> : `[`KmeMessage`](./index.html)`<`[`T`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-video-module-message/index.html#T)`>` |

