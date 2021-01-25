---
title: KmeChatModuleMessage - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.ws.message.module](../index.html) / [KmeChatModuleMessage](./index.html)

# KmeChatModuleMessage

`class KmeChatModuleMessage<T : `[`ChatPayload`](-chat-payload/index.html)`> : `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<`[`T`](index.html#T)`>`

### Types

| [ChatPayload](-chat-payload/index.html) | `class ChatPayload : `[`Payload`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/-payload/index.html) |
| [CreateDmConversationPayload](-create-dm-conversation-payload/index.html) | `data class CreateDmConversationPayload : `[`ChatPayload`](-chat-payload/index.html) |
| [CreatedDmConversationPayload](-created-dm-conversation-payload/index.html) | `data class CreatedDmConversationPayload : `[`ChatPayload`](-chat-payload/index.html) |
| [DeleteMessagePayload](-delete-message-payload/index.html) | `data class DeleteMessagePayload : `[`ChatPayload`](-chat-payload/index.html) |
| [GetConversationPayload](-get-conversation-payload/index.html) | `data class GetConversationPayload : `[`ChatPayload`](-chat-payload/index.html) |
| [GotConversationPayload](-got-conversation-payload/index.html) | `data class GotConversationPayload : `[`ChatPayload`](-chat-payload/index.html) |
| [LoadConversationPayload](-load-conversation-payload/index.html) | `data class LoadConversationPayload : `[`ChatPayload`](-chat-payload/index.html) |
| [LoadMessagesPayload](-load-messages-payload/index.html) | `data class LoadMessagesPayload : `[`ChatPayload`](-chat-payload/index.html) |
| [ReceiveConversationsPayload](-receive-conversations-payload/index.html) | `data class ReceiveConversationsPayload : `[`ChatPayload`](-chat-payload/index.html) |
| [ReceiveMessagePayload](-receive-message-payload/index.html) | `data class ReceiveMessagePayload : `[`ChatPayload`](-chat-payload/index.html) |
| [SendMessagePayload](-send-message-payload/index.html) | `data class SendMessagePayload : `[`ChatPayload`](-chat-payload/index.html) |

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeChatModuleMessage()` |

### Inherited Properties

| [constraint](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/constraint.html) | `var constraint: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KmeConstraint`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-constraint/index.html)`>?` |
| [module](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/module.html) | `var module: `[`KmeMessageModule`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-module/index.html)`?` |
| [name](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/name.html) | `var name: `[`KmeMessageEvent`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-event/index.html)`?` |
| [payload](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/payload.html) | `var payload: `[`T`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html#T)`?` |
| [type](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/type.html) | `var type: `[`KmeMessageEventType`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message-event-type/index.html)`?` |

### Extension Functions

| [toType](../../com.kme.kaltura.kmesdk/to-type.html) | `fun <T> `[`KmeMessage`](../../com.kme.kaltura.kmesdk.ws.message/-kme-message/index.html)`<*>.toType(): `[`T`](../../com.kme.kaltura.kmesdk/to-type.html#T)`?` |

