---
title: KmeSignInApiService.login - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeSignInApiService](index.html) / [login](./login.html)

# login

`@FormUrlEncoded @POST("signin/login") abstract suspend fun login(@Field("LoginForm[email]") email: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Field("LoginForm[password]") password: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`KmeLoginResponse`](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-login-response/index.html)

Login user by input data

### Parameters

`email` - email of a user

`password` - password of a user

**Return**
[KmeLoginResponse](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-login-response/index.html) object in success case

