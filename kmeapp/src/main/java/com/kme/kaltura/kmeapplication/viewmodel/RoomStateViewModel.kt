package com.kme.kaltura.kmeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmeapplication.util.LiveEvent
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.content.KmeContentView
import com.kme.kaltura.kmesdk.module.IKmeContentModule
import com.kme.kaltura.kmesdk.module.IKmeRoomModule
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeRoomExitReason
import com.kme.kaltura.kmesdk.ws.message.module.KmeBannersModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeBannersModuleMessage.BannersPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeBannersModuleMessage.RoomPasswordStatusReceivedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.room.KmeRoomMetaData
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType

class RoomStateViewModel(
    private val kmeSdk: KME
) : ViewModel(), IKmeRoomModule.IKmeRoomStateListener {

    private val isLoading = MutableLiveData<Boolean>()
    val isLoadingLiveData get() = isLoading as LiveData<Boolean>

    private val isConnected = MutableLiveData<Boolean>()
    val isConnectedLiveData get() = isConnected as LiveData<Boolean>

    private val joinedRoom = MutableLiveData<Nothing>()
    val joinedRoomLiveData get() = joinedRoom as LiveData<Nothing>

    private val awaitApproval = MutableLiveData<Nothing>()
    val awaitApprovalLiveData get() = awaitApproval as LiveData<Nothing>

    private val userApproved = MutableLiveData<Nothing>()
    val userApprovedLiveData get() = userApproved as LiveData<Nothing>

    private val userRejected = MutableLiveData<Nothing>()
    val userRejectedLiveData get() = userRejected as LiveData<Nothing>

    private val error = MutableLiveData<String?>()
    val errorLiveData get() = error as LiveData<String?>

    private val roomParticipantLimitReached =
        MutableLiveData<KmeRoomInitModuleMessage<RoomParticipantLimitReachedPayload>?>()
    val roomParticipantLimitReachedLiveData
        get() = roomParticipantLimitReached as LiveData<KmeRoomInitModuleMessage<RoomParticipantLimitReachedPayload>?>

    private val anyInstructorsIsConnected =
        MutableLiveData<KmeRoomInitModuleMessage<AnyInstructorsIsConnectedToRoomPayload>>()
    val anyInstructorsIsConnectedLiveData
        get() = anyInstructorsIsConnected as LiveData<KmeRoomInitModuleMessage<AnyInstructorsIsConnectedToRoomPayload>>

    private val roomHasPassword = MutableLiveData<KmeBannersModuleMessage<BannersPayload>?>()
    val roomHasPasswordLiveData
        get() = roomHasPassword as LiveData<KmeBannersModuleMessage<BannersPayload>?>

    private val roomPasswordStatus =
        MutableLiveData<KmeBannersModuleMessage<RoomPasswordStatusReceivedPayload>?>()
    val roomPasswordStatusLiveData
        get() = roomPasswordStatus as LiveData<KmeBannersModuleMessage<RoomPasswordStatusReceivedPayload>?>

    private val instructorIsOffline =
        MutableLiveData<KmeRoomInitModuleMessage<InstructorIsOfflinePayload>?>()
    val instructorIsOfflineLiveData
        get() = instructorIsOffline as LiveData<KmeRoomInitModuleMessage<InstructorIsOfflinePayload>?>

    private val handRaised = MutableLiveData<Boolean>()
    val handRaisedLiveData get() = handRaised as LiveData<Boolean>

    private val closeConnection = LiveEvent<KmeRoomExitReason>()
    val closeConnectionLiveData get() = closeConnection

    private val roomStateLoaded = MutableLiveData<RoomStatePayload>()
    val roomStateLoadedLiveData get() = roomStateLoaded as LiveData<RoomStatePayload>

    private val sharedContent = MutableLiveData<KmeContentView>()
    val sharedContentLiveData get() = sharedContent as LiveData<KmeContentView>

    private val youModerator = MutableLiveData<Boolean>()
    val youModeratorLiveData get() = youModerator as LiveData<Boolean>

    private var companyId: Long = 0
    private var roomId: Long = 0
    private lateinit var roomAlias: String

    fun reconnect() {
        if (isConnected.value == false && companyId != 0L && roomId != 0L && ::roomAlias.isInitialized) {
            connect(companyId, roomId, roomAlias)
        }
    }

    fun connect(companyId: Long, roomId: Long, roomAlias: String) {
        this.companyId = companyId
        this.roomId = roomId
        this.roomAlias = roomAlias

        isLoading.value = true
        kmeSdk.roomController.connect(roomId, roomAlias, companyId, "1.5", true, this)
        kmeSdk.roomController.subscribeForContent(object : IKmeContentModule.KmeContentListener {
            override fun onContentAvailable(view: KmeContentView) {
                sharedContent.value = view
            }

            override fun onContentNotAvailable(type: KmeContentType) {
                sharedContent.value = null
            }
        })
    }

//    override fun onOpen() {
//        youModerator.value = (isAdmin() || isModerator())
//
//        isLoading.value = false
//        isConnected.value = true
//
//        if (!isAdmin() || isModerator()) {
//            handRaised.value = false
//        }
//
//        kmeSdk.roomController.listen(
//            roomStateHandler,
//            KmeMessageEvent.ROOM_STATE
//        )
//
//        kmeSdk.roomController.listen(
//            bannersHandler,
//            KmeMessageEvent.ANY_INSTRUCTORS_IS_CONNECTED_TO_ROOM,
//            KmeMessageEvent.ROOM_HAS_PASSWORD,
//            KmeMessageEvent.ROOM_PARTICIPANT_LIMIT_REACHED,
//            KmeMessageEvent.AWAIT_INSTRUCTOR_APPROVAL,
//            KmeMessageEvent.USER_APPROVED_BY_INSTRUCTOR,
//            KmeMessageEvent.USER_REJECTED_BY_INSTRUCTOR,
//            KmeMessageEvent.JOINED_ROOM,
//            KmeMessageEvent.ROOM_PASSWORD_STATUS_RECEIVED,
//            KmeMessageEvent.INSTRUCTOR_IS_OFFLINE,
//            KmeMessageEvent.CLOSE_WEB_SOCKET
//        )
//        kmeSdk.roomController.roomModule.joinRoom(roomId, companyId)
//    }

//    override fun onFailure(throwable: Throwable) {
//        error.value = throwable.localizedMessage
//        isConnected.value = false
//        isLoading.value = false
//    }

//    override fun onClosing(code: Int, reason: String) {
//        isConnected.value = false
//        isLoading.value = false
//    }

//    override fun onClosed(code: Int, reason: String) {
//        isConnected.value = false
//        isLoading.value = false
//    }

    fun submitPassword(password: String) {
        if (password.length in 8..24) {
            kmeSdk.roomController.roomModule.joinRoom(roomId, companyId, password)
        } else {
            roomPasswordStatus.value = null
        }
    }

    fun isModerator() = kmeSdk.userController.isModerator()

    fun isAdmin() = kmeSdk.userController.isAdminFor(companyId)

    private fun getCurrentUserId(): Long =
        kmeSdk.userController.getCurrentUserInfo()?.getUserId() ?: 0

    private val roomStateHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.ROOM_STATE -> {
                    val msg: KmeRoomInitModuleMessage<RoomStatePayload>? = message.toType()
                    roomStateLoaded.value = msg?.payload
                }
                else -> {
                }
            }
        }
    }

    fun raiseHand() {
        handRaised.value?.let { raiseHand ->
            handRaised.value = !raiseHand
            kmeSdk.roomController.participantModule.raiseHand(
                roomId,
                companyId,
                getCurrentUserId(),
                getCurrentUserId(),
                !raiseHand
            )
        }
    }

    fun putHandDownByAdmin() {
        handRaised.value?.let {
            handRaised.value = false
        }
    }

    fun endSessionForEveryone() {
        kmeSdk.roomController.roomModule.endSessionForEveryone(roomId, companyId)
    }

    fun endSession() {
        kmeSdk.roomController.roomModule.endSession(roomId, companyId)
    }

    private val bannersHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.AWAIT_INSTRUCTOR_APPROVAL -> {
                    val msg: KmeRoomInitModuleMessage<ApprovalPayload>? = message.toType()
                    if (msg?.payload?.userId == getCurrentUserId()) {
                        awaitApproval.value = null
                    }
                }
                KmeMessageEvent.USER_APPROVED_BY_INSTRUCTOR -> {
                    val msg: KmeRoomInitModuleMessage<ApprovalPayload>? = message.toType()
                    if (msg?.payload?.userId == getCurrentUserId()) {
                        userApproved.value = null
                    }
                }
                KmeMessageEvent.USER_REJECTED_BY_INSTRUCTOR -> {
                    val msg: KmeRoomInitModuleMessage<ApprovalPayload>? = message.toType()
                    if (msg?.payload?.userId == getCurrentUserId()) {
                        userRejected.value = null
                        disconnect()
                    }
                }
                KmeMessageEvent.JOINED_ROOM -> {
                    joinedRoom.value = null
                }
                KmeMessageEvent.ANY_INSTRUCTORS_IS_CONNECTED_TO_ROOM -> {
                    anyInstructorsIsConnected.value = message.toType()
                }
                KmeMessageEvent.ROOM_HAS_PASSWORD -> {
                    roomHasPassword.value = message.toType()
                }
                KmeMessageEvent.ROOM_PASSWORD_STATUS_RECEIVED -> {
                    roomPasswordStatus.value = message.toType()
                }
                KmeMessageEvent.INSTRUCTOR_IS_OFFLINE -> {
                    instructorIsOffline.value = message.toType()
                }
                KmeMessageEvent.ROOM_PARTICIPANT_LIMIT_REACHED -> {
                    roomParticipantLimitReached.value = message.toType()
                }
                KmeMessageEvent.CLOSE_WEB_SOCKET -> {
                    closeConnection.value = message.toType()
                }
                else -> {
                }
            }
        }
    }

    override fun onRoomAvailable(room: KmeRoomMetaData) {

    }

    override fun onRoomBanner(
        event: KmeMessageEvent,
//        payload: KmeRoomInitModuleMessage.RoomInitPayload
    ) {

    }

    override fun onRoomTermsNeeded() {

    }

    override fun onRoomExit(reason: KmeRoomExitReason) {
        closeConnection.value = reason
    }

    override fun onRoomUnavailable(throwable: Throwable?) {

    }

    private fun disconnect() {
//        kmeSdk.roomController.disconnect()
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

}
