# KME SDK user guide

## Initialization
For SDK initialization you should get an instance of main SDK class

```
 val kmeSDK = KME.getInstance()
```

## For each kind of interaction with KME SDK has a specific controllers
* Sign In
* User
* Room
* Notes
* Recordings
* Chat
* Audio

## Sign In
```
 kmeSDK.signInController.login(email, password,
     success = {
         // Handle success result of login. Save access token for next REST calls
         val accessToken = it.data?.accessToken
     }, error = {
         // Handle error result of login.
         val errorMessage = it.message
     })
```

## Get user information
```
 kmeSDK.userController.getUserInformation(success = {
     // Handle success result.
     val userCompanies = it.data?.userCompanies?.companies ?: emptyList()
 }, error = {
     // Handle error result.
     val errorMessage = it.message
 })
```

## Get rooms
```
 kmeSDK.roomController.getRooms(companyId, 0, 20,
     success = {
         // Handle success result.
         val rooms = it.data?.rooms ?: emptyList()
     }, error = {
         // Handle error result.
         val errorMessage = it.message
     })
```

## Fetch WebRTC server information
                    
```
 kmeSDK.roomController.getWebRTCLiveServer(
     roomAlias,
     success = {
         isLoading.value = false
         // Handle success result.
         val webRTC = it.data?.let { kmeWebRTCServer ->
             connectToRoom(kmeWebRTCServer)
         }
     }, error = {
         // Handle error result.
         val errorMessage = it.message
     }
 )
```

## Connect to the room via socket
```
 kmeSDK.roomController.connect(wssUrl, companyId, roomId, true, token, this)
```

## Once socket is connected listen events
```
 override fun onOpen() {
     kmeSDK.roomController.listen(
         bannersHandler,
         KmeMessageEvent.ANY_INSTRUCTORS_IS_CONNECTED_TO_ROOM,
         KmeMessageEvent.ROOM_HAS_PASSWORD,
         KmeMessageEvent.ROOM_PARTICIPANT_LIMIT_REACHED,
         KmeMessageEvent.AWAIT_INSTRUCTOR_APPROVAL,
         KmeMessageEvent.USER_APPROVED_BY_INSTRUCTOR,
         KmeMessageEvent.USER_REJECTED_BY_INSTRUCTOR,
         KmeMessageEvent.JOINED_ROOM,
         KmeMessageEvent.ROOM_PASSWORD_STATUS_RECEIVED,
         KmeMessageEvent.INSTRUCTOR_IS_OFFLINE,
         KmeMessageEvent.CLOSE_WEB_SOCKET,
     )
 }
```

## Sending messages via socket
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
