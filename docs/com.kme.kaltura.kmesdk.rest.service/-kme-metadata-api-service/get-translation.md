---
title: KmeMetadataApiService.getTranslation - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeMetadataApiService](index.html) / [getTranslation](./get-translation.html)

# getTranslation

`@GET("fe/trans") abstract suspend fun getTranslation(@Query("lang") lang: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`GetTranslationsResponse`](../../com.kme.kaltura.kmesdk.rest.response.metadata/-get-translations-response/index.html)

Getting translations strings for specific language

### Parameters

`lang` - language for translations

**Return**
[GetTranslationsResponse](../../com.kme.kaltura.kmesdk.rest.response.metadata/-get-translations-response/index.html) object in success case

