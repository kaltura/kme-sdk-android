---
title: KmeStreamingModuleMessage - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.ws.message.module](../index.html) / [KmeStreamingModuleMessage](./index.html)

# KmeStreamingModuleMessage

`class KmeStreamingModuleMessage<T : `[`StreamingPayload`](-streaming-payload/index.html)`> : `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<`[`T`](index.html#T)`>`

### Types

| [IceGatheringPublishDonePayload](-ice-gathering-publish-done-payload/index.html) | `data class IceGatheringPublishDonePayload : `[`StreamingPayload`](-streaming-payload/index.html) |
| [IceGatheringViewingDonePayload](-ice-gathering-viewing-done-payload/index.html) | `data class IceGatheringViewingDonePayload : `[`StreamingPayload`](-streaming-payload/index.html) |
| [SdpAnswerToFromViewer](-sdp-answer-to-from-viewer/index.html) | `data class SdpAnswerToFromViewer : `[`StreamingPayload`](-streaming-payload/index.html) |
| [SdpAnswerToPublisherPayload](-sdp-answer-to-publisher-payload/index.html) | `data class SdpAnswerToPublisherPayload : `[`StreamingPayload`](-streaming-payload/index.html) |
| [SdpOfferToViewerPayload](-sdp-offer-to-viewer-payload/index.html) | `data class SdpOfferToViewerPayload : `[`StreamingPayload`](-streaming-payload/index.html) |
| [StartPublishingPayload](-start-publishing-payload/index.html) | `data class StartPublishingPayload : `[`StreamingPayload`](-streaming-payload/index.html) |
| [StartViewingPayload](-start-viewing-payload/index.html) | `data class StartViewingPayload : `[`StreamingPayload`](-streaming-payload/index.html) |
| [StartedPublishPayload](-started-publish-payload/index.html) | `data class StartedPublishPayload : `[`StreamingPayload`](-streaming-payload/index.html) |
| [StreamingPayload](-streaming-payload/index.html) | `class StreamingPayload : `[`Payload`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/-payload/index.html) |
| [UserDisconnectedPayload](-user-disconnected-payload/index.html) | `data class UserDisconnectedPayload : `[`StreamingPayload`](-streaming-payload/index.html) |

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeStreamingModuleMessage()` |

### Inherited Properties

| [constraint](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/constraint.html) | `var constraint: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KmeConstraint`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-constraint/index.html)`>?` |
| [module](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/module.html) | `var module: `[`KmeMessageModule`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-module/index.html)`?` |
| [name](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/name.html) | `var name: `[`KmeMessageEvent`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-event/index.html)`?` |
| [payload](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/payload.html) | `var payload: `[`T`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html#T)`?` |
| [type](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/type.html) | `var type: `[`KmeMessageEventType`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-event-type/index.html)`?` |

### Extension Functions

| [toType](../../com.kme.kaltura.kmesdk/to-type.html) | `fun <T> `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<*>.toType(): `[`T`](../../com.kme.kaltura.kmesdk/to-type.html#T)`?` |

