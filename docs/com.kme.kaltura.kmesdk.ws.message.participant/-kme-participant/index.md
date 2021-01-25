---
title: KmeParticipant - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.ws.message.participant](../index.html) / [KmeParticipant](./index.html)

# KmeParticipant

`data class KmeParticipant : `[`Parcelable`](https://developer.android.com/reference/android/os/Parcelable.html)

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeParticipant(userId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`? = null, regionId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`? = null, userType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, avatar: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, userRole: `[`KmeUserRole`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-user-role/index.html)`? = null, fullName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, regionName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, joinTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`? = null, connectionState: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, liveMediaState: `[`KmeMediaDeviceState`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-media-device-state/index.html)`? = null, webcamState: `[`KmeMediaDeviceState`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-media-device-state/index.html)`? = null, micState: `[`KmeMediaDeviceState`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-media-device-state/index.html)`? = null, timeHandRaised: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`? = null, lastUnmuteTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`? = null, deviceType: `[`KmePlatformType`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-platform-type/index.html)`? = null, browser: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, country: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, city: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, managingServerId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`? = null, outOfTabFocus: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`? = null, userPermissions: `[`KmeSettingsV2`](../../com.kme.kaltura.kmesdk.rest.response.room.settings/-kme-settings-v2/index.html)`? = null, isModerator: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`?, isCaptioner: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`?, lat: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`? = null, long: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`? = null, isSpeaking: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, isHandRaised: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)` |

### Properties

| [avatar](avatar.html) | `var avatar: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [browser](browser.html) | `var browser: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [city](city.html) | `var city: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [connectionState](connection-state.html) | `var connectionState: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [country](country.html) | `var country: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [deviceType](device-type.html) | `var deviceType: `[`KmePlatformType`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-platform-type/index.html)`?` |
| [fullName](full-name.html) | `var fullName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [isCaptioner](is-captioner.html) | `var isCaptioner: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`?` |
| [isHandRaised](is-hand-raised.html) | `var isHandRaised: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isModerator](is-moderator.html) | `var isModerator: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`?` |
| [isSpeaking](is-speaking.html) | `var isSpeaking: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [joinTime](join-time.html) | `var joinTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?` |
| [lastUnmuteTime](last-unmute-time.html) | `var lastUnmuteTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?` |
| [lat](lat.html) | `var lat: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`?` |
| [liveMediaState](live-media-state.html) | `var liveMediaState: `[`KmeMediaDeviceState`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-media-device-state/index.html)`?` |
| [long](long.html) | `var long: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`?` |
| [managingServerId](managing-server-id.html) | `var managingServerId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?` |
| [micState](mic-state.html) | `var micState: `[`KmeMediaDeviceState`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-media-device-state/index.html)`?` |
| [outOfTabFocus](out-of-tab-focus.html) | `var outOfTabFocus: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`?` |
| [regionId](region-id.html) | `var regionId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?` |
| [regionName](region-name.html) | `var regionName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [timeHandRaised](time-hand-raised.html) | `var timeHandRaised: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?` |
| [userId](user-id.html) | `var userId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`?` |
| [userPermissions](user-permissions.html) | `var userPermissions: `[`KmeSettingsV2`](../../com.kme.kaltura.kmesdk.rest.response.room.settings/-kme-settings-v2/index.html)`?` |
| [userRole](user-role.html) | `var userRole: `[`KmeUserRole`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-user-role/index.html)`?` |
| [userType](user-type.html) | `var userType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [webcamState](webcam-state.html) | `var webcamState: `[`KmeMediaDeviceState`](../../com.kme.kaltura.kmesdk.ws.message.type/-kme-media-device-state/index.html)`?` |

