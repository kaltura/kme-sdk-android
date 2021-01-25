---
title: KmeSignInApiService.guest - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeSignInApiService](index.html) / [guest](./guest.html)

# guest

`@FormUrlEncoded @POST("signin/guest") abstract suspend fun guest(@Field("Guest[name]") name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Field("Guest[email]") email: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Field("Guest[room_alias]") roomAlias: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Field("room_alias") roomAliasField: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`KmeGuestLoginResponse`](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-guest-login-response/index.html)

Login user by input data and allow to connect to the room

### Parameters

`name` - name of a user

`email` - email of a user

`roomAlias` - alias of a room

`roomAliasField` - alias of a room

**Return**
[KmeGuestLoginResponse](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-guest-login-response/index.html) object in success case

