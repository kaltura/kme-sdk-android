---
title: IKmeUserController.getUserInformation - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeUserController](index.html) / [getUserInformation](./get-user-information.html)

# getUserInformation

`abstract fun getUserInformation(success: (response: `[`KmeGetUserInfoResponse`](../../com.kme.kaltura.kmesdk.rest.response.user/-kme-get-user-info-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Getting actual user information

### Parameters

`success` - function to handle success result. Contains [KmeGetUserInfoResponse](../../com.kme.kaltura.kmesdk.rest.response.user/-kme-get-user-info-response/index.html) object

`error` - function to handle error result. Contains [KmeApiException](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html) object`abstract fun getUserInformation(roomAlias: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, success: (response: `[`KmeGetUserInfoResponse`](../../com.kme.kaltura.kmesdk.rest.response.user/-kme-get-user-info-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Getting actual user information for specific room by alias

### Parameters

`roomAlias` - alias of a room

`success` - function to handle success result. Contains [KmeGetUserInfoResponse](../../com.kme.kaltura.kmesdk.rest.response.user/-kme-get-user-info-response/index.html) object

`error` - function to handle error result. Contains [KmeApiException](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html) object