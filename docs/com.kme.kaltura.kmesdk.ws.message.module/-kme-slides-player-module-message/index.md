---
title: KmeSlidesPlayerModuleMessage - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.ws.message.module](../index.html) / [KmeSlidesPlayerModuleMessage](./index.html)

# KmeSlidesPlayerModuleMessage

`class KmeSlidesPlayerModuleMessage<T : `[`SlidePlayerPayload`](-slide-player-payload/index.html)`> : `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<`[`T`](index.html#T)`>`

### Types

| [SlideChangedPayload](-slide-changed-payload/index.html) | `data class SlideChangedPayload : `[`SlidePlayerPayload`](-slide-player-payload/index.html) |
| [SlidePlayerPayload](-slide-player-payload/index.html) | `class SlidePlayerPayload : `[`Payload`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/-payload/index.html) |

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeSlidesPlayerModuleMessage()` |

### Inherited Properties

| [constraint](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/constraint.html) | `var constraint: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KmeConstraint`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-constraint/index.html)`>?` |
| [module](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/module.html) | `var module: `[`KmeMessageModule`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-module/index.html)`?` |
| [name](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/name.html) | `var name: `[`KmeMessageEvent`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-event/index.html)`?` |
| [payload](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/payload.html) | `var payload: `[`T`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html#T)`?` |
| [type](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/type.html) | `var type: `[`KmeMessageEventType`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-event-type/index.html)`?` |

### Extension Functions

| [toType](../../com.kme.kaltura.kmesdk/to-type.html) | `fun <T> `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<*>.toType(): `[`T`](../../com.kme.kaltura.kmesdk/to-type.html#T)`?` |

