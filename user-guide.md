# KME SDK user guide

### Getting the SDK
Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Step 2. Add the dependency
```gradle
dependencies {
	       implementation 'com.github.kaltura:kme-sdk-android:1.0.11'
}
```
### Initialization
For SDK initialization the application should get an instance of main SDK class
```
 val kmeSDK = KME.getInstance()
```

The app should make sure that internet connection is available before calling:
```
 kmeSDK.initSDK(
     applicationContext,
     success = {
         // Now the app can use controllers from the SDK
     },
     error = {
         // Handle error
     }
 )
```

### For each kind of interaction with KME SDK has a specific controllers
* Sign In - [`KmeSignInController`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/IKmeSignInController.html)
* User - [`KmeUserController`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/IKmeUserController.html)
* Room - [`KmeRoomController`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeRoomController.html)

# Sign In
```
 kmeSDK.signInController.login(
    email,
    password,
    success = {
        isLoading.value = false
        loginResponse.value = it.data
    },
    error = {
        isLoading.value = false
        loginError.value = it.message
    }
 )
```

# Get user information
```
 kmeSDK.userController.getUserInformation(
     success = {
         val userCompanies = it.data?.userCompanies?.companies ?: emptyList()
     },
     error = {
         val errorMessage = it.message
     }
 )
```

### Get rooms
```
 kmeSDK.roomController.getRooms(
     companyId,
     0,
     20,
     success = {
        val rooms = it.data?.rooms ?: emptyList()
     },
     error = {
        val errorMessage = it.message
     }
 )
```

# Room

### Connect to the room
The application can connect to the room using WS by calling `KmeRoomController.connect()` API
```
 private fun connectToRoom(roomId: Long, roomAlias: String, companyId: Long) {
     kmeSDK.roomController.connect(roomId, roomAlias, companyId, isReconnect = true, object : IKmeWSConnectionListener {
         override fun onOpen() {
             // Handle socket opened
         }
 
         override fun onFailure(throwable: Throwable) {
             // Handle socket failure
         }
 
         override fun onClosing(code: Int, reason: String) {
             // Handle socket closing
         }
 
         override fun onClosed(code: Int, reason: String) {
             // Handle socket closed
         }
     })
 }
```

Once WS is connected the application can listen events from the room.
```
 override fun onOpen() {
     kmeSDK.roomController.listen(object : IKmeMessageListener {
         override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
             // Handle incoming message
         }},
         KmeMessageEvent.JOIN_ROOM,
         KmeMessageEvent.ROOM_STATE
     )
 }
```

### Parsing WS messages
Basically all messages from the server are coming as json string. KME SDK provides a mechanism to receive them as parsed objects depends on `KmeMessageEvent`.
```
 override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
     when (message.name) {
         KmeMessageEvent.ROOM_STATE -> {
             val msg: KmeRoomInitModuleMessage<RoomStatePayload>? = message.toType()
         }
         KmeMessageEvent.AWAIT_INSTRUCTOR_APPROVAL -> {
             val msg: KmeRoomInitModuleMessage<ApprovalPayload>? = message.toType()
         }
         KmeMessageEvent.USER_STARTED_TO_PUBLISH -> {
             val msg: KmeStreamingModuleMessage<StartedPublishPayload>? = message.toType()
         }
         KmeMessageEvent.DESKTOP_SHARE_STATE_UPDATED -> {
             val msg: KmeDesktopShareModuleMessage<DesktopShareStateUpdatedPayload>? = message.toType()
         }
         else -> {
            // ....
         }
     }
 }
```

### WS events.
All available events and objects to parse supported by KME SDK are described in the `KmeMessageEvent` and separates by sections:
 * Room initialization, Banners - [`KmeRoomInitModuleMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeRoomInitModuleMessage.html)
 * Participants - [`KmeParticipantsModuleMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeParticipantsModuleMessage.html)
 * Chat - [`KmeChatModuleMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeChatModuleMessage.html)
 * Notes - [`KmeRoomNotesMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeRoomNotesMessage.html)
 * Settings - [`KmeRoomSettingsModuleMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeRoomSettingsModuleMessage.html)
 * Recording - [`KmeRoomRecordingMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeRoomRecordingMessage.html)

### Room modules
All actions inside the room are described by set of room modules from [`KmeRoomController`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeRoomController.html):
 * [`KmeRoomModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeRoomModule.html)
 * [`KmePeerConnectionModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmePeerConnectionModule.html)
 * [`KmeParticipantModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeParticipantModule.html)
 * [`KmeChatModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeChatModule.html)
 * [`KmeNoteModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeNoteModule.html)
 * [`KmeRecordingModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeRecordingModule.html)
 * [`KmeAudioModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeAudioModule.html)

### Publishing own video stream
 To start publish/view video stream the application need to initialize [`KmePeerConnectionModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmePeerConnectionModule.html) first:
```
 kmeSdk.roomController.peerConnectionModule.initialize(roomId, companyId, this)
 
 kmeSDK.roomController.peerConnectionModule.addPublisher(userId, rendererView)
```

### View participant's video stream
 To start view participant's video stream need to send via WS a message
```
 kmeSDK.roomController.peerConnectionModule.addViewer(userId, rendererView)
```

### Active content
 The application can listen room active content changes by calling `KmeRoomController.subscribeForContent()` API
```
 kmeSdk.roomController.subscribeForContent(object : IKmeContentModule.KmeContentListener {
     override fun onContentAvailable(view: KmeContentView) {
         // Content available
     }

     override fun onContentNotAvailable() {
         // Content no longer available
     }
 })
```
 [`KmeContentView`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/content/room/KmeContentView.html) is a view for representing shared content. Supported types of content:
 * Whiteboard
 * Slides
 * Media content
 * Desktop share
