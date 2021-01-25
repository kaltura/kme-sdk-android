---
title: KmeApiException - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest](../index.html) / [KmeApiException](./index.html)

# KmeApiException

`sealed class KmeApiException : `[`Exception`](https://developer.android.com/reference/java/lang/Exception.html)

Main REST exceptions class. Indicates an error produced by REST response handling

### Exceptions

| [HttpException](-http-exception/index.html) | `open class HttpException : `[`KmeApiException`](./index.html) |
| [InternalApiException](-internal-api-exception/index.html) | `class InternalApiException : `[`KmeApiException`](./index.html) |
| [NetworkException](-network-exception/index.html) | `class NetworkException : `[`KmeApiException`](./index.html) |
| [ParseJsonException](-parse-json-exception/index.html) | `class ParseJsonException : `[`KmeApiException`](./index.html) |
| [SomethingBadHappenedException](-something-bad-happened-exception/index.html) | `class SomethingBadHappenedException : `[`KmeApiException`](./index.html) |

### Properties

| [message](message.html) | `open val message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |

### Inheritors

| [HttpException](-http-exception/index.html) | `open class HttpException : `[`KmeApiException`](./index.html) |
| [InternalApiException](-internal-api-exception/index.html) | `class InternalApiException : `[`KmeApiException`](./index.html) |
| [NetworkException](-network-exception/index.html) | `class NetworkException : `[`KmeApiException`](./index.html) |
| [ParseJsonException](-parse-json-exception/index.html) | `class ParseJsonException : `[`KmeApiException`](./index.html) |
| [SomethingBadHappenedException](-something-bad-happened-exception/index.html) | `class SomethingBadHappenedException : `[`KmeApiException`](./index.html) |

