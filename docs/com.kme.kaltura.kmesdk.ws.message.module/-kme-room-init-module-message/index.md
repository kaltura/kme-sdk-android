---
title: KmeRoomInitModuleMessage - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.ws.message.module](../index.html) / [KmeRoomInitModuleMessage](./index.html)

# KmeRoomInitModuleMessage

`class KmeRoomInitModuleMessage<T : `[`RoomInitPayload`](-room-init-payload/index.html)`> : `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<`[`T`](index.html#T)`>`

### Types

| [AnyInstructorsIsConnectedToRoomPayload](-any-instructors-is-connected-to-room-payload/index.html) | `data class AnyInstructorsIsConnectedToRoomPayload : `[`RoomInitPayload`](-room-init-payload/index.html) |
| [ApprovalPayload](-approval-payload/index.html) | `data class ApprovalPayload : `[`RoomInitPayload`](-room-init-payload/index.html) |
| [CloseWebSocketPayload](-close-web-socket-payload/index.html) | `data class CloseWebSocketPayload : `[`RoomInitPayload`](-room-init-payload/index.html) |
| [InstructorIsOfflinePayload](-instructor-is-offline-payload/index.html) | `data class InstructorIsOfflinePayload : `[`RoomInitPayload`](-room-init-payload/index.html) |
| [JoinRoomPayload](-join-room-payload/index.html) | `data class JoinRoomPayload : `[`RoomInitPayload`](-room-init-payload/index.html) |
| [JoinedRoomPayload](-joined-room-payload/index.html) | `data class JoinedRoomPayload : `[`RoomInitPayload`](-room-init-payload/index.html) |
| [NewUserJoinedPayload](-new-user-joined-payload/index.html) | `data class NewUserJoinedPayload : `[`RoomInitPayload`](-room-init-payload/index.html) |
| [RoomAvailableForParticipantPayload](-room-available-for-participant-payload/index.html) | `data class RoomAvailableForParticipantPayload : `[`RoomInitPayload`](-room-init-payload/index.html) |
| [RoomInitPayload](-room-init-payload/index.html) | `class RoomInitPayload : `[`Payload`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/-payload/index.html) |
| [RoomParticipantLimitReachedPayload](-room-participant-limit-reached-payload/index.html) | `data class RoomParticipantLimitReachedPayload : `[`RoomInitPayload`](-room-init-payload/index.html) |
| [RoomStatePayload](-room-state-payload/index.html) | `data class RoomStatePayload : `[`RoomInitPayload`](-room-init-payload/index.html) |

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeRoomInitModuleMessage()` |

### Inherited Properties

| [constraint](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/constraint.html) | `var constraint: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KmeConstraint`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-constraint/index.html)`>?` |
| [module](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/module.html) | `var module: `[`KmeMessageModule`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-module/index.html)`?` |
| [name](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/name.html) | `var name: `[`KmeMessageEvent`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-event/index.html)`?` |
| [payload](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/payload.html) | `var payload: `[`T`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html#T)`?` |
| [type](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/type.html) | `var type: `[`KmeMessageEventType`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-event-type/index.html)`?` |

### Extension Functions

| [toType](../../com.kme.kaltura.kmesdk/to-type.html) | `fun <T> `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<*>.toType(): `[`T`](../../com.kme.kaltura.kmesdk/to-type.html#T)`?` |

