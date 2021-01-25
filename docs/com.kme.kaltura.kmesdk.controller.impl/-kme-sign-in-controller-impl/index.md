---
title: KmeSignInControllerImpl - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller.impl](../index.html) / [KmeSignInControllerImpl](./index.html)

# KmeSignInControllerImpl

`class KmeSignInControllerImpl : `[`KmeController`](../-kme-controller/index.html)`, `[`IKmeSignInController`](../../com.kme.kaltura.kmesdk.controller/-i-kme-sign-in-controller/index.html)

An implementation for signIn/signUp

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeSignInControllerImpl()`<br>An implementation for signIn/signUp |

### Functions

| [guest](guest.html) | `fun guest(name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, email: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, roomAlias: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, success: (response: `[`KmeGuestLoginResponse`](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-guest-login-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Login user by input data and allow to connect to the room |
| [login](login.html) | `fun login(email: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, password: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, success: (response: `[`KmeLoginResponse`](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-login-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Login user by input data |
| [logout](logout.html) | `fun logout(success: (response: `[`KmeLogoutResponse`](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-logout-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Logout from actual user |
| [register](register.html) | `fun register(fullName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, email: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, password: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, forceRegister: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, addToMailingList: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, success: (response: `[`KmeRegisterResponse`](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-register-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Registers new user by input data |

### Companion Object Properties

| [PASS_ENCRYPT_KEY](-p-a-s-s_-e-n-c-r-y-p-t_-k-e-y.html) | `const val PASS_ENCRYPT_KEY: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

