---
title: IKmeRoomRecordingController - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeRoomRecordingController](./index.html)

# IKmeRoomRecordingController

`interface IKmeRoomRecordingController`

An interface for recording in the room

### Functions

| [checkRecordingLicense](check-recording-license.html) | `abstract fun checkRecordingLicense(roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, success: (response: `[`KmeCheckRecordingLicenseResponse`](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-check-recording-license-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Checking recording license for the room |

### Inheritors

| [KmeRoomRecordingControllerImpl](../../com.kme.kaltura.kmesdk.controller.impl/-kme-room-recording-controller-impl/index.html) | `class KmeRoomRecordingControllerImpl : `[`KmeController`](../../com.kme.kaltura.kmesdk.controller.impl/-kme-controller/index.html)`, `[`IKmeRoomRecordingController`](./index.html)<br>An implementation for recording in the room |

