---
title: KmeChatApiService.changePublicChatVisibility - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeChatApiService](index.html) / [changePublicChatVisibility](./change-public-chat-visibility.html)

# changePublicChatVisibility

`@GET("room/setRoomSetting") abstract suspend fun changePublicChatVisibility(@Query("room_id") roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, @Query("module") module: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Query("key") key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Query("value") value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`KmeChangeRoomSettingsResponse`](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-change-room-settings-response/index.html)

Change visibility of public chat

### Parameters

`roomId` - id of a room

`module` - KME module name

`key` - KME chat key

`value` - value flag

**Return**
[KmeChangeRoomSettingsResponse](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-change-room-settings-response/index.html) object in success case

