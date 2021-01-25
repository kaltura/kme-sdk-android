---
title: IKmeChatController - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeChatController](./index.html)

# IKmeChatController

`interface IKmeChatController`

An interface for actions related to chat

### Functions

| [changePublicChatVisibility](change-public-chat-visibility.html) | `abstract fun changePublicChatVisibility(roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, value: `[`KmePermissionValue`](../../com.kme.kaltura.kmesdk.ws.message.type.permissions/-kme-permission-value/index.html)`, success: (response: `[`KmeChangeRoomSettingsResponse`](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-change-room-settings-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Change visibility of public chat |

### Inheritors

| [KmeChatControllerImpl](../../com.kme.kaltura.kmesdk.controller.impl/-kme-chat-controller-impl/index.html) | `class KmeChatControllerImpl : `[`KmeController`](../../com.kme.kaltura.kmesdk.controller.impl/-kme-controller/index.html)`, `[`IKmeChatController`](./index.html)<br>An implementation actions related to chat |

