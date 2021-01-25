---
title: IKmeSignInController.logout - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeSignInController](index.html) / [logout](./logout.html)

# logout

`abstract fun logout(success: (response: `[`KmeLogoutResponse`](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-logout-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Logout from actual user

### Parameters

`success` - function to handle success result. Contains [KmeLogoutResponse](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-logout-response/index.html) object

`error` - function to handle error result. Contains [KmeApiException](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html) object