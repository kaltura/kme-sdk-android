---
title: KmeConversation - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.ws.message.chat](../index.html) / [KmeConversation](./index.html)

# KmeConversation

`data class KmeConversation : `[`Parcelable`](https://developer.android.com/reference/android/os/Parcelable.html)

### Types

| [User](-user/index.html) | `data class User : `[`Parcelable`](https://developer.android.com/reference/android/os/Parcelable.html) |

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeConversation(id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, lastMessage: `[`KmeChatMessage`](../-kme-chat-message/index.html)`? = null, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, conversationType: `[`KmeConversationType`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-conversation-type/index.html)`? = null, isSystem: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`? = null, avatar: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, unreadMessages: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`? = null, firstUser: `[`User`](-user/index.html)`? = null, secondUser: `[`User`](-user/index.html)`? = null)` |

### Properties

| [avatar](avatar.html) | `val avatar: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [conversationType](conversation-type.html) | `val conversationType: `[`KmeConversationType`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-conversation-type/index.html)`?` |
| [firstUser](first-user.html) | `val firstUser: `[`User`](-user/index.html)`?` |
| [hasAccess](has-access.html) | `var hasAccess: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [id](id.html) | `val id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [isSystem](is-system.html) | `val isSystem: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`?` |
| [lastMessage](last-message.html) | `val lastMessage: `[`KmeChatMessage`](../-kme-chat-message/index.html)`?` |
| [name](name.html) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [secondUser](second-user.html) | `val secondUser: `[`User`](-user/index.html)`?` |
| [unreadMessages](unread-messages.html) | `val unreadMessages: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`?` |

