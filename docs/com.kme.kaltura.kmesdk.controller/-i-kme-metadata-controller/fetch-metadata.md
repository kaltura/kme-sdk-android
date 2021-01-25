---
title: IKmeMetadataController.fetchMetadata - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeMetadataController](index.html) / [fetchMetadata](./fetch-metadata.html)

# fetchMetadata

`abstract fun fetchMetadata(success: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Getting metadata for global usage

### Parameters

`success` - function to handle success result

`error` - function to handle error result. Contains [KmeApiException](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html) object