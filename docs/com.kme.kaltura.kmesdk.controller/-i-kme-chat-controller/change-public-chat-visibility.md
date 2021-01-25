---
title: IKmeChatController.changePublicChatVisibility - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeChatController](index.html) / [changePublicChatVisibility](./change-public-chat-visibility.html)

# changePublicChatVisibility

`abstract fun changePublicChatVisibility(roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, value: `[`KmePermissionValue`](../../com.kme.kaltura.kmesdk.ws.message.type.permissions/-kme-permission-value/index.html)`, success: (response: `[`KmeChangeRoomSettingsResponse`](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-change-room-settings-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Change visibility of public chat

### Parameters

`roomId` - id of a room

`value` - value flag

`success` - function to handle success result. Contains [KmeChangeRoomSettingsResponse](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-change-room-settings-response/index.html) object

`error` - function to handle error result. Contains [KmeApiException](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html) object