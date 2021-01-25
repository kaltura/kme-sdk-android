---
title: IKmeRoomRecordingController.checkRecordingLicense - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeRoomRecordingController](index.html) / [checkRecordingLicense](./check-recording-license.html)

# checkRecordingLicense

`abstract fun checkRecordingLicense(roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, success: (response: `[`KmeCheckRecordingLicenseResponse`](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-check-recording-license-response/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Checking recording license for the room

### Parameters

`roomId` - id of a room

`success` - function to handle success result. Contains [KmeCheckRecordingLicenseResponse](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-check-recording-license-response/index.html) object

`error` - function to handle error result. Contains [KmeApiException](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html) object