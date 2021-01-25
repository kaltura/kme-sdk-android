---
title: IKmeMetadataController.getTranslation - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeMetadataController](index.html) / [getTranslation](./get-translation.html)

# getTranslation

`abstract fun getTranslation(lang: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, success: (response: `[`GetTranslationsResponse`](../../com.kme.kaltura.kmesdk.rest.response.metadata/-get-translations-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Getting translations strings for specific language

### Parameters

`lang` - language for translations

`success` - function to handle success result. Contains [GetTranslationsResponse](../../com.kme.kaltura.kmesdk.rest.response.metadata/-get-translations-response/index.html) object

`error` - function to handle error result. Contains [KmeApiException](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html) object