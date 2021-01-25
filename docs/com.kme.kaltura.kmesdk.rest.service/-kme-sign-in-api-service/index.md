---
title: KmeSignInApiService - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeSignInApiService](./index.html)

# KmeSignInApiService

`interface KmeSignInApiService`

An interface for signIn/signUp API calls

### Functions

| [guest](guest.html) | `abstract suspend fun guest(name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, email: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, roomAlias: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, roomAliasField: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`KmeGuestLoginResponse`](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-guest-login-response/index.html)<br>Login user by input data and allow to connect to the room |
| [login](login.html) | `abstract suspend fun login(email: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, password: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`KmeLoginResponse`](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-login-response/index.html)<br>Login user by input data |
| [logout](logout.html) | `abstract suspend fun logout(): `[`KmeLogoutResponse`](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-logout-response/index.html)<br>Logout from actual user |
| [register](register.html) | `abstract suspend fun register(fullName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, email: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, password: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, forceRegister: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, addToMailingList: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`KmeRegisterResponse`](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-register-response/index.html)<br>Registers new user by input data |

