---
title: IKmeMetadataController - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeMetadataController](./index.html)

# IKmeMetadataController

`interface IKmeMetadataController`

An interface for actions related to metadata

### Functions

| [fetchMetadata](fetch-metadata.html) | `abstract fun fetchMetadata(success: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Getting metadata for global usage |
| [getMetadata](get-metadata.html) | `abstract fun getMetadata(): `[`KmeMetadata`](../../com.kme.kaltura.kmesdk.rest.response.metadata/-kme-metadata/index.html)`?`<br>Getting stored metadata |
| [getTranslation](get-translation.html) | `abstract fun getTranslation(lang: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, success: (response: `[`GetTranslationsResponse`](../../com.kme.kaltura.kmesdk.rest.response.metadata/-get-translations-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Getting translations strings for specific language |

### Inheritors

| [KmeMetadataControllerImpl](../../com.kme.kaltura.kmesdk.controller.impl/-kme-metadata-controller-impl/index.html) | `class KmeMetadataControllerImpl : KmeKoinComponent, `[`IKmeMetadataController`](./index.html)<br>An implementation for actions related to metadata |

