# KME SDK user guide

### Building
Make sure application and SDK folders are stay on the same directory level

### Initialization
For SDK initialization the application should get an instance of main SDK class
```
 val kmeSDK = KME.getInstance()
```

### For each kind of interaction with KME SDK has a specific controllers
* Sign In - [`KmeSignInController`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/IKmeSignInController.html)
* User - [`KmeUserController`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/IKmeUserController.html)
* Room - [`KmeRoomController`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeRoomController.html)

### Sign In
```
 kmeSDK.signInController.login(email, password, success = {
     val accessToken = it.data?.accessToken
 }, error = {
     val errorMessage = it.message
 })
```

### Get user information
```
 kmeSDK.userController.getUserInformation(success = {
     val userCompanies = it.data?.userCompanies?.companies ?: emptyList()
 }, error = {
     val errorMessage = it.message
 })
```

### Get rooms
```
 kmeSDK.roomController.getRooms(companyId, 0, 20, success = {
     val rooms = it.data?.rooms ?: emptyList()
 }, error = {
     val errorMessage = it.message
 })
```

# Room
To get an information for connection to the room the application side should ask for WebRTC server info first.

### Fetch WebRTC server information
```
 kmeSDK.roomController.getWebRTCLiveServer(roomAlias, success = {
     it.data?.let { connectToRoom(it) }
 }, error = {
     val errorMessage = it.message
 })
```

### Connect to the room using WS
Based on information from WebRTC server the application can connect to the room using WS by calling `KmeRoomController.connect()` API
```
 private fun connectToRoom(webRTCServer: KmeWebRTCServer, companyId: Long, roomId: Long) {
     val wssUrl = webRTCServer.wssUrl
     val token = webRTCServer.token
     kmeSDK.roomController.connect(wssUrl, companyId, roomId, isReconnect = true, token, object : IKmeWSConnectionListener {
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
 * Streaming - [`KmeStreamingModuleMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeStreamingModuleMessage.html)
 * Chat - [`KmeChatModuleMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeChatModuleMessage.html)
 * Notes - [`KmeRoomNotesMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeRoomNotesMessage.html)
 * Settings - [`KmeRoomSettingsModuleMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeRoomSettingsModuleMessage.html)
 * Active Content - [`KmeActiveContentModuleMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeActiveContentModuleMessage.html)
   * Slides - [`KmeSlidesPlayerModuleMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeSlidesPlayerModuleMessage.html)
   * Video - [`KmeVideoModuleMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeVideoModuleMessage.html)
   * Desktop sharing - [`KmeDesktopShareModuleMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeDesktopShareModuleMessage.html)
   * White board - [``](https://kaltura.github.io/kme-sdk-android/)
 * Recording - [`KmeRoomRecordingMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeRoomRecordingMessage.html)

### Room modules
All actions inside the room are described by set of room modules from [`KmeRoomController`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeRoomController.html):
 * [`KmeRoomModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeRoomModule.html)
 * [`KmePeerConnectionModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmePeerConnectionModule.html)
 * [`KmeParticipantModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeParticipantModule.html)
 * [`KmeChatModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeChatModule.html)
 * [`KmeNoteModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeNoteModule.html)
 * [`KmeRecordingModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeRecordingModule.html)
 * [`KmeDesktopShareModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeDesktopShareModule.html)
 * [`KmeAudioModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmeAudioModule.html)

### Publishing own video stream
 To start publish/view video stream the application need to initialize [`KmePeerConnectionModule`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/controller/room/IKmePeerConnectionModule.html) first:
```
 kmeSDK.roomController.peerConnectionModule.initialize(
     roomId, companyId,
     turnUrl, turnUser, turnCred,
     peerConnectionEventsHandler
 )
 
 kmeSDK.roomController.peerConnectionModule.addPublisher(userId, rendererView)
```

### View participant's video stream
 To start view participant's video stream need to send via WS a message
```
 kmeSDK.roomController.peerConnectionModule.addViewer(userId, rendererView)
```

### Active content
 Content showed by administrator in the room handles by [`KmeActiveContentModuleMessage`](https://kaltura.github.io/kme-sdk-android/com/kme/kaltura/kmesdk/ws/message/module/KmeActiveContentModuleMessage.html) message
```
 kmeSdk.roomController.listen(
        activeContentHandler,
        KmeMessageEvent.INIT_ACTIVE_CONTENT,
        KmeMessageEvent.SET_ACTIVE_CONTENT
 )

 private val activeContentHandler = object : IKmeMessageListener {
     override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
         when (message.name) {
             KmeMessageEvent.INIT_ACTIVE_CONTENT,
             KmeMessageEvent.SET_ACTIVE_CONTENT -> {
                 val contentMessage: KmeActiveContentModuleMessage<SetActiveContentPayload>? = message.toType()
                 when (contentMessage.contentType) {
                      VIDEO, AUDIO, YOUTUBE -> {
                          // Add equivalent UI. Subscribe for video state messages
                      }
                      IMAGE, SLIDES -> {
                          // Add equivalent UI. Subscribe for slides messages
                      }
                      DESKTOP_SHARE -> {
                          // Add equivalent UI. Subscribe for desktop share peerconnection messages
                      }
                 }
             }
         }
     }
 }
```
 