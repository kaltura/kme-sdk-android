---
title: IKmeUserController - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeUserController](./index.html)

# IKmeUserController

`interface IKmeUserController`

An interface for actual user information details

### Functions

| [getCurrentParticipant](get-current-participant.html) | `abstract fun getCurrentParticipant(): `[`KmeParticipant`](../../com.kme.kaltura.kmesdk.ws.message.participant/-kme-participant/index.html)`?`<br>Getting stored user information in the room |
| [getCurrentUserInfo](get-current-user-info.html) | `abstract fun getCurrentUserInfo(): `[`KmeUserInfoData`](../../com.kme.kaltura.kmesdk.rest.response.user/-kme-user-info-data/index.html)`?`<br>Getting stored user information |
| [getUserInformation](get-user-information.html) | `abstract fun getUserInformation(success: (response: `[`KmeGetUserInfoResponse`](../../com.kme.kaltura.kmesdk.rest.response.user/-kme-get-user-info-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Getting actual user information`abstract fun getUserInformation(roomAlias: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, success: (response: `[`KmeGetUserInfoResponse`](../../com.kme.kaltura.kmesdk.rest.response.user/-kme-get-user-info-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Getting actual user information for specific room by alias |
| [isAdminFor](is-admin-for.html) | `abstract fun isAdminFor(companyId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Checks is actual user has admin permissions for specific company |
| [isLoggedIn](is-logged-in.html) | `abstract fun isLoggedIn(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Checks is actual user is logged and access token exist |
| [isModerator](is-moderator.html) | `abstract fun isModerator(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Checks is actual user has moderator permissions |
| [logout](logout.html) | `abstract fun logout(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Removes actual user information |
| [updateParticipant](update-participant.html) | `abstract fun updateParticipant(participant: `[`KmeParticipant`](../../com.kme.kaltura.kmesdk.ws.message.participant/-kme-participant/index.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Updates actual user info in the room |

### Inheritors

| [KmeUserControllerImpl](../../com.kme.kaltura.kmesdk.controller.impl/-kme-user-controller-impl/index.html) | `class KmeUserControllerImpl : `[`KmeController`](../../com.kme.kaltura.kmesdk.controller.impl/-kme-controller/index.html)`, `[`IKmeUserController`](./index.html)<br>An implementation for actual user information details |

