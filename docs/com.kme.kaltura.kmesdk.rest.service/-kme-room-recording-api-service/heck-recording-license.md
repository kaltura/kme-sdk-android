---
title: KmeRoomRecordingApiService.heckRecordingLicense - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.rest.service](../index.html) / [KmeRoomRecordingApiService](index.html) / [heckRecordingLicense](./heck-recording-license.html)

# heckRecordingLicense

`@GET("company/CheckRecordingLicenseToCompanyByRoom") abstract suspend fun heckRecordingLicense(@Query("room_id") roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`KmeCheckRecordingLicenseResponse`](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-check-recording-license-response/index.html)

Checking recording license for the room

### Parameters

`roomId` - id of a room

**Return**
[KmeCheckRecordingLicenseResponse](../../com.kme.kaltura.kmesdk.rest.response.room/-kme-check-recording-license-response/index.html) object in success case

