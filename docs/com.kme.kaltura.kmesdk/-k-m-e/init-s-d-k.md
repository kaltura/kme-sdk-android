---
title: KME.initSDK - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk](../index.html) / [KME](index.html) / [initSDK](./init-s-d-k.html)

# initSDK

`fun initSDK(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`, success: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Initialization function. Initializes all needed controllers and modules.
In the same place - fetching metadata from the server to use it in future REST API calls.

### Parameters

`context` - application context

`success` - function to handle success initialization event

`error` - function to handle error initialization event