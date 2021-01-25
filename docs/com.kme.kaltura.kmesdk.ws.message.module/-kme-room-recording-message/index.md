---
title: KmeRoomRecordingMessage - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.ws.message.module](../index.html) / [KmeRoomRecordingMessage](./index.html)

# KmeRoomRecordingMessage

`class KmeRoomRecordingMessage<T : `[`RecordingPayload`](-recording-payload/index.html)`> : `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<`[`T`](index.html#T)`>`

### Types

| [RecordingCompletedPayload](-recording-completed-payload/index.html) | `data class RecordingCompletedPayload : `[`RecordingRxPayload`](-recording-rx-payload/index.html) |
| [RecordingConversionCompletedPayload](-recording-conversion-completed-payload/index.html) | `data class RecordingConversionCompletedPayload : `[`RecordingRxPayload`](-recording-rx-payload/index.html) |
| [RecordingFailurePayload](-recording-failure-payload/index.html) | `data class RecordingFailurePayload : `[`RecordingRxPayload`](-recording-rx-payload/index.html) |
| [RecordingInitiatedPayload](-recording-initiated-payload/index.html) | `data class RecordingInitiatedPayload : `[`RecordingRxPayload`](-recording-rx-payload/index.html) |
| [RecordingPayload](-recording-payload/index.html) | `class RecordingPayload : `[`Payload`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/-payload/index.html) |
| [RecordingRxPayload](-recording-rx-payload/index.html) | `class RecordingRxPayload : `[`RecordingPayload`](-recording-payload/index.html) |
| [RecordingStartPayload](-recording-start-payload/index.html) | `data class RecordingStartPayload : `[`RecordingTxPayload`](-recording-tx-payload/index.html) |
| [RecordingStartedPayload](-recording-started-payload/index.html) | `data class RecordingStartedPayload : `[`RecordingRxPayload`](-recording-rx-payload/index.html) |
| [RecordingStartingPayload](-recording-starting-payload/index.html) | `data class RecordingStartingPayload : `[`RecordingRxPayload`](-recording-rx-payload/index.html) |
| [RecordingStatusPayload](-recording-status-payload/index.html) | `data class RecordingStatusPayload : `[`RecordingRxPayload`](-recording-rx-payload/index.html) |
| [RecordingStopPayload](-recording-stop-payload/index.html) | `data class RecordingStopPayload : `[`RecordingTxPayload`](-recording-tx-payload/index.html) |
| [RecordingStoppedPayload](-recording-stopped-payload/index.html) | `data class RecordingStoppedPayload : `[`RecordingRxPayload`](-recording-rx-payload/index.html) |
| [RecordingTxPayload](-recording-tx-payload/index.html) | `class RecordingTxPayload : `[`RecordingPayload`](-recording-payload/index.html) |
| [RecordingUploadCompletedPayload](-recording-upload-completed-payload/index.html) | `data class RecordingUploadCompletedPayload : `[`RecordingRxPayload`](-recording-rx-payload/index.html) |

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeRoomRecordingMessage()` |

### Inherited Properties

| [constraint](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/constraint.html) | `var constraint: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KmeConstraint`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-constraint/index.html)`>?` |
| [module](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/module.html) | `var module: `[`KmeMessageModule`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-module/index.html)`?` |
| [name](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/name.html) | `var name: `[`KmeMessageEvent`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-event/index.html)`?` |
| [payload](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/payload.html) | `var payload: `[`T`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html#T)`?` |
| [type](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/type.html) | `var type: `[`KmeMessageEventType`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-event-type/index.html)`?` |

### Extension Functions

| [toType](../../com.kme.kaltura.kmesdk/to-type.html) | `fun <T> `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<*>.toType(): `[`T`](../../com.kme.kaltura.kmesdk/to-type.html#T)`?` |

