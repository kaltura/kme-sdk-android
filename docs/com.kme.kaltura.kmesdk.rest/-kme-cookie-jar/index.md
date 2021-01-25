---
title: KmeCookieJar - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest](../index.html) / [KmeCookieJar](./index.html)

# KmeCookieJar

`class KmeCookieJar : CookieJar`

Provides **policy** and **persistence** for HTTP cookies.

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeCookieJar(prefs: `[`IKmePreferences`](../../com.kme.kaltura.kmesdk.prefs/-i-kme-preferences/index.html)`)`<br>Provides **policy** and **persistence** for HTTP cookies. |

### Functions

| [loadForRequest](load-for-request.html) | `fun loadForRequest(url: HttpUrl): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<Cookie>`<br>Load cookies from the jar for an HTTP request to [url](load-for-request.html#com.kme.kaltura.kmesdk.rest.KmeCookieJar$loadForRequest(okhttp3.HttpUrl)/url). This method returns a possibly empty list of cookies for the network request. |
| [saveFromResponse](save-from-response.html) | `fun saveFromResponse(url: HttpUrl, cookieList: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<Cookie>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Save cookies only from metadata response |

