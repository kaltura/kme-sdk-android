# KME SDK user guide

### Initialization
For SDK initialization the applciation should get an instance of main SDK class
```
 val kmeSDK = KME.getInstance()
```

#### For each kind of interaction with KME SDK has a specific controllers
* Sign In (`kmeSDK.signInController`)
* User (`kmeSDK.userController`)
* Room (`kmeSDK.roomController`)
* Notes (`kmeSDK.roomNotesController`)
* Recordings (`kmeSDK.roomRecordingController`)
* Chat (`kmeSDK.chatController`)
* Audio (`kmeSDK.audioController`)

#### Sign In
```
 kmeSDK.signInController.login(email, password, success = {
     val accessToken = it.data?.accessToken
 }, error = {
     val errorMessage = it.message
 })
```

#### Get user information
```
 kmeSDK.userController.getUserInformation(success = {
     val userCompanies = it.data?.userCompanies?.companies ?: emptyList()
 }, error = {
     val errorMessage = it.message
 })
```

#### Get rooms
```
 kmeSDK.roomController.getRooms(companyId, 0, 20, success = {
     val rooms = it.data?.rooms ?: emptyList()
 }, error = {
     val errorMessage = it.message
 })
```

# Room
To get an information for connection to the room the application side should ask for WebRTC server info first.

#### Fetch WebRTC server information
```
 kmeSDK.roomController.getWebRTCLiveServer(roomAlias, success = {
     it.data?.let { connectToRoom(it) }
 }, error = {
     val errorMessage = it.message
 })
```

#### Connect to the room using WS
Based on information from WebRTC server the applciation can connect to the room using WS by calling `kmeSDK.roomController.connect()` API
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

#### Parsing WS messages
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

#### WS events.
All available events and objects to parse supported by KME SDK are described in the `KmeMessageEvent` and separetes by sections:
* Room initialization, Banners - `KmeRoomInitModuleMessage`
* Participants - `KmeParticipantsModuleMessage`
* Streaming - `KmeStreamingModuleMessage`
* Chat - `KmeChatModuleMessage`
* Notes - `KmeRoomNotesMessage`
* Settings - `KmeRoomSettingsModuleMessage`
* Active Content - `KmeActiveContentModuleMessage`
  * Slides - `KmeSlidesPlayerModuleMessage`
  * Video - `KmeVideoModuleMessage`
  * Desktop sharing - `KmeDesktopShareModuleMessage`
  * White board - ``
* Recording - `KmeRoomRecordingMessage`

#### Sending messages via socket
```
 kmeSdk.roomController.send(buildJoinRoomMessage(roomId, companyId))

 fun buildJoinRoomMessage(
     roomId: Long,
     companyId: Long
 ): KmeRoomInitModuleMessage<JoinRoomPayload> {
     return KmeRoomInitModuleMessage<JoinRoomPayload>().apply {
         module = KmeMessageModule.ROOM_INIT
         name = KmeMessageEvent.JOIN_ROOM
         type = KmeMessageEventType.CALLBACK
         payload = JoinRoomPayload("load", roomId, companyId)
     }
 }
```
