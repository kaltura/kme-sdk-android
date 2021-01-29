# KME SDK user guide

### Initialization
For SDK initialization the application should get an instance of main SDK class
```
 val kmeSDK = KME.getInstance()
```

### For each kind of interaction with KME SDK has a specific controllers
* Sign In - `kmeSDK.signInController`
* User - `kmeSDK.userController`
* Room - `kmeSDK.roomController`
* Notes - `kmeSDK.roomNotesController`
* Recordings - `kmeSDK.roomRecordingController`
* Chat - `kmeSDK.chatController`
* Audio - `kmeSDK.audioController`

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

### Sending messages via socket
```
 kmeSDK.roomController.send(buildJoinRoomMessage(roomId, companyId))

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

### Publishing video stream
1. To start publish own video stream need to send via WS a message
```
kmeSDK.roomController.send(buildMediaInitMessage(roomId, publisherId, companyId))

 fun buildMediaInitMessage(
     roomId: Long,
     userId: Long,
     companyId: Long
 ): KmeParticipantsModuleMessage<UserMediaStateInitPayload> {
     return KmeParticipantsModuleMessage<UserMediaStateInitPayload>().apply {
         constraint = listOf(KmeConstraint.INCLUDE_SELF)
         module = KmeMessageModule.ROOM_PARTICIPANTS
         name = KmeMessageEvent.USER_MEDIA_STATE_INIT
         type = KmeMessageEventType.BROADCAST
         payload = UserMediaStateInitPayload(
             userId,
             roomId,
             companyId,
             KmeMediaDeviceState.LIVE_INIT,
             KmeMediaDeviceState.LIVE,
             KmeMediaDeviceState.LIVE
         )
     }
 }
```

2. Add publisher peerconnection using SDK API
```
 kmeSDK.roomController.addPublisherPeerConnection(publisherId, localRenderer, this)
```

3. Once peerconnection created the app should ask the SDK to create a SDP offer
```
 override fun onPeerConnectionCreated(requestedUserIdStream: String) {
     kmeSDK.roomController.getPublisherConnection()?.createOffer()
 }
```

4. When SDP is created `onLocalDescription()` callback invoked by the SDK. The app should provide it to the server.
```
 override fun onLocalDescription(requestedUserIdStream: String, mediaServerId: Long, sdp: String, type: String) {
     kmeSDK.roomController.send(buildStartPublishingMessage(roomId,  publisherId, companyId, type, sdp))
 }

 fun buildStartPublishingMessage(
     roomId: Long,
     userId: Long,
     companyId: Long,
     sdpType: String,
     sdpDescription: String
 ): KmeStreamingModuleMessage<StartPublishingPayload> {
     return KmeStreamingModuleMessage<StartPublishingPayload>().apply {
         module = KmeMessageModule.STREAMING
         name = KmeMessageEvent.START_PUBLISHING
         type = KmeMessageEventType.VOID
         payload = StartPublishingPayload(
             roomId,
             userId,
             companyId,
             StreamingPayload.SDP(sdpType, sdpDescription),
             "publisher"
         )
     }
 }
```

5. The app should waiting for `KmeMessageEvent.SDP_ANSWER_TO_PUBLISHER` event and set payloads data to the appropriate SDK API
```
 override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
     when (message.name) {
         KmeMessageEvent.SDP_ANSWER_TO_PUBLISHER -> {
             val msg: KmeStreamingModuleMessage<SdpAnswerToPublisherPayload>? = message.toType()
             val publisher = kmeSdk.roomController.getPublisherConnection()
             msg?.payload?.mediaServerId?.let {
                 publisher?.setMediaServerId(it)
             }
             msg?.payload?.sdpAnswer?.let {
                 publisher?.setRemoteSdp(KmeSdpType.ANSWER, it)
             }
         }
     }
 }
```

6. When SDK is ready to setup p2p connection `onIceGatheringDone()` is invoked, and the aap should notify the server about it. It is the last point for in video publishing sequence.
```
 override fun onIceGatheringDone(requestedUserIdStream: String, mediaServerId: Long) {
     kmeSDK.roomController.send(buildGatheringPublishDoneMessage(roomId, publisherId, companyId, mediaServerId))
 }

 fun buildGatheringPublishDoneMessage(
     roomId: Long,
     userId: Long,
     companyId: Long,
     mediaServerId: Long
 ): KmeStreamingModuleMessage<IceGatheringPublishDonePayload> {
     return KmeStreamingModuleMessage<IceGatheringPublishDonePayload>().apply {
         module = KmeMessageModule.STREAMING
         name = KmeMessageEvent.ICE_GATHERING_DONE
         type = KmeMessageEventType.VOID
         payload = IceGatheringPublishDonePayload(
             roomId,
             userId,
             companyId,
             mediaServerId,
             "videoroom",
             "publisher"
         )
     }
 }
```

### View participant's video stream
1. To start view participant's video stream need to send via WS a message
```
kmeSdk.roomController.send(buildStartViewingMessage(roomId, publisherId, companyId, userId))

fun buildStartViewingMessage(
    roomId: Long,
    userId: Long,
    companyId: Long,
    requestedUserIdStream: String?
): KmeStreamingModuleMessage<StartViewingPayload> {
    return KmeStreamingModuleMessage<StartViewingPayload>().apply {
        module = KmeMessageModule.STREAMING
        name = KmeMessageEvent.START_VIEWING
        type = KmeMessageEventType.VOID
        payload = StartViewingPayload(
            roomId,
            userId,
            companyId,
            requestedUserIdStream,
            "viewer"
        )
    }
}
```

2. Server will answer on it with needed SDP for establishing p2p connection
```
 override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
     when (message.name) {
         KmeMessageEvent.SDP_OFFER_FOR_VIEWER -> {
             val msg: KmeStreamingModuleMessage<SdpOfferToViewerPayload>? = message.toType()
             msg?.payload?.requestedUserIdStream?.toLongOrNull()?.let { idStream ->
                  kmeSdk.roomController.addViewerPeerConnection(userId, rendererView, this)?.apply {
                      setMediaServerId(mediaServerId)
                      setRemoteSdp(KmeSdpType.OFFER, description)
                  }
             }
         }
     }
 }
```

3. After setting remote SDP by `setRemoteSdp()` API peerconnection generates a viewr SDP. The app should provide it to the server 
```
 override fun onLocalDescription(requestedUserIdStream: String, mediaServerId: Long, sdp: String, type: String) {
     kmeSDK.roomController.send(buildAnswerFromViewerMessage(
         roomId,
         publisherId,
         companyId,
         type,
         sdp,
         requestedUserIdStream,
         mediaServerId))
 }
     
 fun buildAnswerFromViewerMessage(
     roomId: Long,
     userId: Long,
     companyId: Long,
     sdpType: String,
     sdpDescription: String,
     requestedUserIdStream: String,
     mediaServerId: Long,
 ): KmeStreamingModuleMessage<SdpAnswerToFromViewer> {
     return KmeStreamingModuleMessage<SdpAnswerToFromViewer>().apply {
         module = KmeMessageModule.STREAMING
         name = KmeMessageEvent.FORWARD_SDP_ANSWER_FROM_VIEWER
         type = KmeMessageEventType.VOID
         payload = SdpAnswerToFromViewer(
             roomId,
             userId,
             companyId,
             "videoroom",
             StreamingPayload.SDP(sdpType, sdpDescription),
             requestedUserIdStream,
             mediaServerId,
             "viewer"
         )
     }
 }
```

4. When SDK is ready to setup p2p connection `onIceGatheringDone()` is invoked, and the aap should notify the server about it. It is the last point for in video viewing sequence.
```
 override fun onIceGatheringDone(requestedUserIdStream: String, mediaServerId: Long) {
     kmeSDK.roomController.send(buildGatheringPublishDoneMessage(roomId, publisherId, companyId, mediaServerId))
 }

 fun buildGatheringViewDoneMessage(
     roomId: Long,
     userId: Long,
     companyId: Long,
     requestedUserIdStream: String,
     mediaServerId: Long
 ): KmeStreamingModuleMessage<IceGatheringViewingDonePayload> {
     return KmeStreamingModuleMessage<IceGatheringViewingDonePayload>().apply {
         module = KmeMessageModule.STREAMING
         name = KmeMessageEvent.ICE_GATHERING_DONE
         type = KmeMessageEventType.VOID
         payload = IceGatheringViewingDonePayload(
             roomId,
             userId,
             companyId,
             mediaServerId,
             "videoroom",
             requestedUserIdStream,
             "viewer"
         )
     }
 }
```
