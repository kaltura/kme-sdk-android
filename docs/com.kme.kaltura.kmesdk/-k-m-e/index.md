---
title: KME - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk](../index.html) / [KME](./index.html)

# KME

`class KME : KmeKoinComponent`

Main class for KME SDK.

### Constructors

| [&lt;init&gt;](-init-.html) | `KME()`<br>Main class for KME SDK. |

### Properties

| [audioController](audio-controller.html) | `val audioController: `[`IKmeAudioController`](../../com.kme.kaltura.kmesdk.controller/-i-kme-audio-controller/index.html) |
| [chatController](chat-controller.html) | `val chatController: `[`IKmeChatController`](../../com.kme.kaltura.kmesdk.controller/-i-kme-chat-controller/index.html) |
| [isSDKInitialized](is-s-d-k-initialized.html) | `var isSDKInitialized: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [roomController](room-controller.html) | `val roomController: `[`IKmeRoomController`](../../com.kme.kaltura.kmesdk.controller/-i-kme-room-controller/index.html) |
| [roomNotesController](room-notes-controller.html) | `val roomNotesController: `[`IKmeRoomNotesController`](../../com.kme.kaltura.kmesdk.controller/-i-kme-room-notes-controller/index.html) |
| [roomRecordingController](room-recording-controller.html) | `val roomRecordingController: `[`IKmeRoomRecordingController`](../../com.kme.kaltura.kmesdk.controller/-i-kme-room-recording-controller/index.html) |
| [signInController](sign-in-controller.html) | `val signInController: `[`IKmeSignInController`](../../com.kme.kaltura.kmesdk.controller/-i-kme-sign-in-controller/index.html) |
| [userController](user-controller.html) | `val userController: `[`IKmeUserController`](../../com.kme.kaltura.kmesdk.controller/-i-kme-user-controller/index.html) |

### Functions

| [getCookies](get-cookies.html) | `fun getCookies(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [getFilesUrl](get-files-url.html) | `fun getFilesUrl(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [initSDK](init-s-d-k.html) | `fun initSDK(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`, success: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, error: (exception: `[`KmeApiException`](../../com.kme.kaltura.kmesdk.rest/-kme-api-exception/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Initialization function. Initializes all needed controllers and modules. In the same place - fetching metadata from the server to use it in future REST API calls. |

### Companion Object Functions

| [getInstance](get-instance.html) | `fun getInstance(): `[`KME`](./index.html)<br>Instantiate a KME singleton |

