---
title: KmeMetadataControllerImpl - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller.impl](../index.html) / [KmeMetadataControllerImpl](./index.html)

# KmeMetadataControllerImpl

`class KmeMetadataControllerImpl : KmeKoinComponent, `[`IKmeMetadataController`](../../com.kme.kaltura.kmesdk.controller/-i-kme-metadata-controller/index.html)

An implementation for actions related to metadata

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeMetadataControllerImpl()`<br>An implementation for actions related to metadata |

### Functions

| [fetchMetadata](fetch-metadata.html) | `fun fetchMetadata(success: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Getting metadata for global usage |
| [getMetadata](get-metadata.html) | `fun getMetadata(): `[`KmeMetadata`](../../com.kme.kaltura.kmesdk.rest.response.metadata/-kme-metadata/index.html)`?`<br>Getting stored metadata |
| [getTranslation](get-translation.html) | `fun getTranslation(lang: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, success: (response: `[`GetTranslationsResponse`](../../com.kme.kaltura.kmesdk.rest.response.metadata/-get-translations-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Getting translations strings for specific language |

