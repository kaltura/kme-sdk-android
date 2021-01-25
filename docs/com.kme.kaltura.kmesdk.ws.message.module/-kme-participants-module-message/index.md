---
title: KmeParticipantsModuleMessage - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.ws.message.module](../index.html) / [KmeParticipantsModuleMessage](./index.html)

# KmeParticipantsModuleMessage

`class KmeParticipantsModuleMessage<T : `[`ParticipantsPayload`](-participants-payload/index.html)`> : `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<`[`T`](index.html#T)`>`

### Types

| [AllUsersHandPutPayload](-all-users-hand-put-payload/index.html) | `data class AllUsersHandPutPayload : `[`ParticipantsPayload`](-participants-payload/index.html) |
| [ChangeUserFocusEventPayload](-change-user-focus-event-payload/index.html) | `data class ChangeUserFocusEventPayload : `[`ParticipantsPayload`](-participants-payload/index.html) |
| [ParticipantsPayload](-participants-payload/index.html) | `class ParticipantsPayload : `[`Payload`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/-payload/index.html) |
| [SetParticipantModerator](-set-participant-moderator/index.html) | `data class SetParticipantModerator : `[`ParticipantsPayload`](-participants-payload/index.html) |
| [UserMediaStateChangedPayload](-user-media-state-changed-payload/index.html) | `data class UserMediaStateChangedPayload : `[`ParticipantsPayload`](-participants-payload/index.html) |
| [UserMediaStateInitPayload](-user-media-state-init-payload/index.html) | `data class UserMediaStateInitPayload : `[`ParticipantsPayload`](-participants-payload/index.html) |
| [UserRaiseHandPayload](-user-raise-hand-payload/index.html) | `data class UserRaiseHandPayload : `[`ParticipantsPayload`](-participants-payload/index.html) |

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeParticipantsModuleMessage()` |

### Inherited Properties

| [constraint](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/constraint.html) | `var constraint: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KmeConstraint`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-constraint/index.html)`>?` |
| [module](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/module.html) | `var module: `[`KmeMessageModule`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-module/index.html)`?` |
| [name](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/name.html) | `var name: `[`KmeMessageEvent`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-event/index.html)`?` |
| [payload](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/payload.html) | `var payload: `[`T`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html#T)`?` |
| [type](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/type.html) | `var type: `[`KmeMessageEventType`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-event-type/index.html)`?` |

### Extension Functions

| [toType](../../com.kme.kaltura.kmesdk/to-type.html) | `fun <T> `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<*>.toType(): `[`T`](../../com.kme.kaltura.kmesdk/to-type.html#T)`?` |

