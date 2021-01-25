---
title: com.kme.kaltura.kmesdk.rest - kmeSdk
---

[kmeSdk](../index.html) / [com.kme.kaltura.kmesdk.rest](./index.html)

## Package com.kme.kaltura.kmesdk.rest

### Types

| [KmeCookieJar](-kme-cookie-jar/index.html) | `class KmeCookieJar : CookieJar`<br>Provides **policy** and **persistence** for HTTP cookies. |
| [KmeIntToBooleanTypeAdapter](-kme-int-to-boolean-type-adapter/index.html) | `class KmeIntToBooleanTypeAdapter : JsonDeserializer<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>` |
| [KmeStringToBooleanTypeAdapter](-kme-string-to-boolean-type-adapter/index.html) | `class KmeStringToBooleanTypeAdapter : JsonDeserializer<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>` |

### Exceptions

| [KmeApiException](-kme-api-exception/index.html) | `sealed class KmeApiException : `[`Exception`](https://developer.android.com/reference/java/lang/Exception.html)<br>Main REST exceptions class. Indicates an error produced by REST response handling |

### Functions

| [downloadFile](download-file.html) | `suspend fun downloadFile(call: suspend () -> ResponseBody?, target: `[`OutputStream`](https://developer.android.com/reference/java/io/OutputStream.html)`, success: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`Exception`](https://developer.android.com/reference/java/lang/Exception.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): <ERROR CLASS>`<br>Main REST function. Download files from the server |
| [safeApiCall](safe-api-call.html) | `suspend fun <T> safeApiCall(call: suspend () -> `[`T`](safe-api-call.html#T)`, success: (response: `[`T`](safe-api-call.html#T)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Main REST function. Sending requests and handle responses |

