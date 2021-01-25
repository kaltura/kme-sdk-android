---
title: safeApiCall - kmeSdk
---

[kmeSdk](../index.html) / [com.kme.kaltura.kmesdk.rest](index.html) / [safeApiCall](./safe-api-call.html)

# safeApiCall

`suspend fun <T> safeApiCall(call: suspend () -> `[`T`](safe-api-call.html#T)`, success: (response: `[`T`](safe-api-call.html#T)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Main REST function. Sending requests and handle responses

