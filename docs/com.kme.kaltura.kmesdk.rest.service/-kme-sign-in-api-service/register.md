---
title: KmeSignInApiService.register - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeSignInApiService](index.html) / [register](./register.html)

# register

`@FormUrlEncoded @POST("signin/register") abstract suspend fun register(@Field("SignupForm[full_name]") fullName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Field("SignupForm[email]") email: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Field("SignupForm[password]") password: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @Field("SignupForm[forceRegister]") forceRegister: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, @Field("SignupForm[addToMailingList]") addToMailingList: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`KmeRegisterResponse`](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-register-response/index.html)

Registers new user by input data

### Parameters

`fullName` - name of a user

`email` - email of a user

`password` - password of a user

`forceRegister` -

`addToMailingList` - add email to subscriptions

**Return**
[KmeRegisterResponse](../../com.kme.kaltura.kmesdk.rest.response.signin/-kme-register-response/index.html) object in success case

