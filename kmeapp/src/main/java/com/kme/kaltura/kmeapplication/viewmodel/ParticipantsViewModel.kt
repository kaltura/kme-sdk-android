package com.kme.kaltura.kmeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.kme.kaltura.kmeapplication.util.extensions.defineNewDeviceStateByAdmin
import com.kme.kaltura.kmeapplication.util.extensions.ifNonNull
import com.kme.kaltura.kmeapplication.util.extensions.isModerator
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage.NewUserJoinedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage.RoomStatePayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage.RoomSettingsChangedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage.StartedPublishPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage.UserDisconnectedPayload
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaStateType
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

class ParticipantsViewModel(
    private val kmeSdk: KME,
    private val gson: Gson
) : ViewModel() {

    private val allMicsState = MutableLiveData<KmePermissionValue>()
    val allMicsStateLiveData get() = allMicsState as LiveData<KmePermissionValue>

    private val allCamsState = MutableLiveData<KmePermissionValue>()
    val allCamsStateLiveData get() = allCamsState as LiveData<KmePermissionValue>

    private val anyHandsRaisedState = MutableLiveData<Boolean>()
    val anyHandsRaisedStateLiveData get() = anyHandsRaisedState as LiveData<Boolean>

    private val publicChatState = MutableLiveData<KmePermissionValue>()
    val publicChatStateLiveData get() = publicChatState as LiveData<KmePermissionValue>

    private val participantChanged = MutableLiveData<KmeParticipant>()
    val participantChangedLiveData get() = participantChanged as LiveData<KmeParticipant>

    private val userDisconnected = MutableLiveData<Long>()
    val userDisconnectedLiveData get() = userDisconnected as LiveData<Long>

    private val participantMediaStateChanged = MutableLiveData<UserMediaStateChangedPayload>()
    val participantMediaStateChangedLiveData
        get() = participantMediaStateChanged as LiveData<UserMediaStateChangedPayload>

    private val mediaStateChangedByAdmin = MutableLiveData<Nothing>()
    val mediaStateChangedByAdminLiveData get() = mediaStateChangedByAdmin as LiveData<Nothing>

    private val allMicsToggledByAdmin = MutableLiveData<Nothing>()
    val allMicsToggledByAdminLiveData get() = allMicsToggledByAdmin as LiveData<Nothing>

    private val allCamsToggledByAdmin = MutableLiveData<Nothing>()
    val allCamsToggledByAdminLiveData get() = allCamsToggledByAdmin as LiveData<Nothing>

    private val allHandsDownByAdmin = MutableLiveData<Nothing>()
    val allHandsDownByAdminLiveData get() = allHandsDownByAdmin as LiveData<Nothing>

    private val micToggledByAdmin = MutableLiveData<Boolean>()
    val micToggledByAdminLiveData get() = micToggledByAdmin as LiveData<Boolean>

    private val camToggledByAdmin = MutableLiveData<Boolean>()
    val camToggledByAdminLiveData get() = camToggledByAdmin as LiveData<Boolean>

    private val liveToggledByAdmin = MutableLiveData<Boolean>()
    val liveToggledByAdminLiveData get() = liveToggledByAdmin as LiveData<Boolean>

    private val participantHandRaised = MutableLiveData<Pair<Long, Boolean>>()
    val participantHandRaisedLiveData get() = participantHandRaised as LiveData<Pair<Long, Boolean>>

    private var participantsList: MutableList<KmeParticipant> = mutableListOf()
    val participants get() = participantsList as List<KmeParticipant>

    private var raisedHandsList: MutableList<Long> = mutableListOf()
    private var companyId: Long = 0
    private var roomId: Long = 0

    fun setRoomData(companyId: Long, roomId: Long) {
        this.companyId = companyId
        this.roomId = roomId
    }

    fun setRoomState(payload: RoomStatePayload?) {
        participantsList = payload?.participants?.values?.toMutableList() ?: arrayListOf()
    }

    fun subscribe() {
        kmeSdk.roomController.listen(
            participantsHandler,
            KmeMessageEvent.USER_MEDIA_STATE_INIT,
            KmeMessageEvent.USER_MEDIA_STATE_CHANGED,
            KmeMessageEvent.USER_STARTED_TO_PUBLISH,
            KmeMessageEvent.NEW_USER_JOINED,
            KmeMessageEvent.USER_DISCONNECTED,
            KmeMessageEvent.ROOM_SETTINGS_CHANGED,
            KmeMessageEvent.MAKE_ALL_USERS_HAND_PUT,
            KmeMessageEvent.USER_HAND_RAISED
        )
    }

    fun updateAdminPanelState() {
        allMicsState.value =
            kmeSdk.roomController.roomSettings?.roomInfo?.settingsV2?.general?.muteAllMics
        allCamsState.value =
            kmeSdk.roomController.roomSettings?.roomInfo?.settingsV2?.general?.muteAllCams
        publicChatState.value =
            kmeSdk.roomController.roomSettings?.roomInfo?.settingsV2?.chatModule?.defaultSettings?.publicChat
    }

    fun putAllHandsDown() {
        kmeSdk.roomController.participantModule.allHandsDown(roomId, companyId)
    }

    fun toggleAllMics() {
        allMicsState.value?.let {
            if (it == KmePermissionValue.ON) {
                allMicsState.value = KmePermissionValue.OFF
                sendChangeMediaSettingsMessage(
                    KmePermissionKey.MUTE_ALL_MICS,
                    KmePermissionValue.OFF
                )
            } else {
                allMicsState.value = KmePermissionValue.ON
                sendChangeMediaSettingsMessage(
                    KmePermissionKey.MUTE_ALL_MICS,
                    KmePermissionValue.ON
                )
            }
        }
    }

    fun toggleAllCams() {
        allCamsState.value?.let {
            if (it == KmePermissionValue.ON) {
                allCamsState.value = KmePermissionValue.OFF
                sendChangeMediaSettingsMessage(
                    KmePermissionKey.MUTE_ALL_CAMS,
                    KmePermissionValue.OFF
                )
            } else {
                allCamsState.value = KmePermissionValue.ON
                sendChangeMediaSettingsMessage(
                    KmePermissionKey.MUTE_ALL_CAMS,
                    KmePermissionValue.ON
                )
            }
        }
    }

    private fun sendChangeMediaSettingsMessage(key: KmePermissionKey, value: KmePermissionValue) {
        kmeSdk.userController.getCurrentUserInfo()?.getUserId()?.let { userId ->
            kmeSdk.roomController.roomModule.changeRoomSettings(
                roomId,
                userId,
                key,
                value
            )
        }
    }

    fun togglePublicChat() {
        publicChatState.value?.let {
            if (it == KmePermissionValue.ON) {
                sendPublicChatStateChangedMessage(KmePermissionValue.OFF)
            } else {
                sendPublicChatStateChangedMessage(KmePermissionValue.ON)
            }
        }
    }

    private fun sendPublicChatStateChangedMessage(value: KmePermissionValue) {
        kmeSdk.roomController.chatModule.changePublicChatVisibility(roomId, value,
            success = {
                if (it.data.chatModule?.defaultSettings?.publicChat == value) {
                    kmeSdk.roomController.roomSettings?.roomInfo?.settingsV2?.chatModule
                        ?.defaultSettings?.publicChat = value
                    publicChatState.value = value
                }
            },
            error = {
            })
    }

    fun toggleAllMicsByAdmin(enable: Boolean) {
        participantsList.forEach { participant ->
            if (!participant.isModerator()) {
                participant.micState = participant.micState?.defineNewDeviceStateByAdmin(enable)
                if (participant.micState != KmeMediaDeviceState.LIVE) {
                    participant.isSpeaking = false
                }
            }
        }
        allMicsToggledByAdmin.value = null
    }

    fun toggleAllCamsByAdmin(enable: Boolean) {
        participantsList.forEach { participant ->
            if (!participant.isModerator()) {
                participant.webcamState =
                    participant.webcamState?.defineNewDeviceStateByAdmin(enable)
            }
        }
        allCamsToggledByAdmin.value = null
    }

    fun toggleParticipantMicro(participant: KmeParticipant) {
        val newState = participant.micState?.defineNewDeviceStateByAdmin()
        participant.userId?.let {
            if (newState != null) {
                sendChangeMediaStateFor(it, KmeMediaStateType.MIC, newState)
            }
        }
    }

    fun toggleParticipantCam(participant: KmeParticipant) {
        val newState = participant.webcamState?.defineNewDeviceStateByAdmin()
        participant.userId?.let {
            if (newState != null) {
                sendChangeMediaStateFor(it, KmeMediaStateType.WEBCAM, newState)
            }
        }
    }

    fun toggleParticipantLive(participant: KmeParticipant) {
        var newState = participant.liveMediaState?.defineNewDeviceStateByAdmin()

        // map only for LIVE status
        if (newState == KmeMediaDeviceState.LIVE) {
            newState = KmeMediaDeviceState.LIVE_SUCCESS
        }
        participant.userId?.let {
            if (newState != null) {
                sendChangeMediaStateFor(it, KmeMediaStateType.LIVE_MEDIA, newState)
            }
        }
    }

    fun updatePublisherLive() {
        updateUserLive(getCurrentUserId())
    }

    fun updateUserModeratorState(userId: Long, isModerator: Boolean) {
        participantsList.find {
            it.userId == userId
        }?.let { participant ->
            participant.isModerator = isModerator
        }
    }

    private fun sendChangeMediaStateFor(
        id: Long,
        device: KmeMediaStateType,
        state: KmeMediaDeviceState
    ) {
        kmeSdk.roomController.participantModule.changeMediaState(
            roomId,
            companyId,
            id,
            device,
            state
        )
    }

    fun isParticipantJoined(userId: Long): Boolean {
        return participants.find { participant -> participant.userId == userId } != null
    }

    private fun isModerator() = kmeSdk.userController.isModerator()

    private fun isAdmin() = kmeSdk.userController.isAdminFor(companyId)

    private fun getCurrentUserId(): Long =
        kmeSdk.userController.getCurrentUserInfo()?.getUserId() ?: 0

    private val participantsHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.NEW_USER_JOINED -> {
                    val msg: KmeRoomInitModuleMessage<NewUserJoinedPayload>? = message.toType()
                    msg?.let {
                        val json = gson.toJson(it.payload)
                        val participant = gson.fromJson(json, KmeParticipant::class.java)
                        if (participant != null) {
                            addOrUpdateParticipant(participant)
                        }
                    }
                }
                KmeMessageEvent.USER_MEDIA_STATE_INIT -> {
                    val msg: KmeParticipantsModuleMessage<UserMediaStateInitPayload>? =
                        message.toType()
                    msg?.payload?.let { initUserMediaState(it) }
                }
                KmeMessageEvent.USER_MEDIA_STATE_CHANGED -> {
                    val msg: KmeParticipantsModuleMessage<UserMediaStateChangedPayload>? =
                        message.toType()
                    msg?.payload?.let {
                        val currentUserId = kmeSdk.userController.getCurrentParticipant()?.userId
                        if (it.userId == currentUserId) {
                            val state = it.stateValue
                            val enable = state == KmeMediaDeviceState.LIVE

                            when (it.mediaStateType) {
                                KmeMediaStateType.MIC -> micToggledByAdmin.value = enable
                                KmeMediaStateType.WEBCAM -> camToggledByAdmin.value = enable
                                KmeMediaStateType.LIVE_MEDIA -> liveToggledByAdmin.value =
                                    state == KmeMediaDeviceState.LIVE_SUCCESS
                                else -> {
                                }
                            }
                        }
                        updateUserMediaState(it)
                    }
                }
                KmeMessageEvent.USER_STARTED_TO_PUBLISH -> {
                    val msg: KmeStreamingModuleMessage<StartedPublishPayload>? = message.toType()
                    msg?.payload?.userId?.toLongOrNull()?.let { updateUserLive(it) }
                }

                KmeMessageEvent.ROOM_SETTINGS_CHANGED -> {
                    val msg: KmeRoomSettingsModuleMessage<RoomSettingsChangedPayload>? =
                        message.toType()

                    when (msg?.payload?.changedRoomSetting) {
                        KmePermissionKey.MUTE_ALL_MICS,
                        KmePermissionKey.MUTE_ALL_CAMS -> mediaStateChangedByAdmin.value = null
                        else -> {
                        }
                    }
                }
                KmeMessageEvent.USER_HAND_RAISED -> {
                    val msg: KmeParticipantsModuleMessage<UserRaiseHandPayload>? = message.toType()
                    ifNonNull(
                        msg?.payload?.targetUserId,
                        msg?.payload?.isRaise
                    ) { userId, isRaise ->
                        updateRaiseHandState(userId, isRaise)
                        if (isAdmin() || isModerator()) {
                            participantHandRaised.value = Pair(userId, isRaise)
                        }
                    }
                }
                KmeMessageEvent.MAKE_ALL_USERS_HAND_PUT -> {
                    updateAllHandsDown()
                }
                KmeMessageEvent.USER_DISCONNECTED -> {
                    val userDisconnectedMessage: KmeStreamingModuleMessage<UserDisconnectedPayload>? =
                        message.toType()
                    val payload = userDisconnectedMessage?.payload
                    if (payload != null) {
                        removeParticipant(payload)
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun addOrUpdateParticipant(participant: KmeParticipant) {
        participantsList.find {
            it.userId == participant.userId
        }?.let { foundParticipant ->
            participantsList.remove(foundParticipant)
        }
        participantsList.add(participant)
        participantChanged.value = participant
    }

    private fun removeParticipant(payload: UserDisconnectedPayload) {
        val isRemoved =
            participantsList.removeAll { localParticipant -> localParticipant.userId == payload.userId }
        if (isRemoved) {
            userDisconnected.value = payload.userId
        }
    }

    private fun initUserMediaState(payload: UserMediaStateInitPayload) {
        participantsList.find {
            it.userId == payload.userId
        }?.let { participant ->
            participant.micState = payload.micState
            participant.webcamState = payload.webcamState
            participant.liveMediaState = payload.liveMediaState
            participantChanged.value = participant
        }
    }

    private fun updateUserMediaState(payload: UserMediaStateChangedPayload) {
        participantsList.find {
            it.userId == payload.userId
        }?.let { participant ->
            when (payload.mediaStateType) {
                KmeMediaStateType.MIC -> {
                    participant.micState = payload.stateValue
                }
                KmeMediaStateType.WEBCAM -> {
                    participant.webcamState = payload.stateValue
                }
                KmeMediaStateType.LIVE_MEDIA -> {
                    participant.liveMediaState = payload.stateValue
                }
                null -> {
                }
            }
            participantMediaStateChanged.value = payload
            participantChanged.value = participant
        }
    }

    private fun updateUserLive(userId: Long) {
        participantsList.find {
            it.userId == userId
        }?.let { participant ->
            participant.liveMediaState = KmeMediaDeviceState.LIVE_SUCCESS
            participantChanged.value = participant
        }
    }

    private fun updateRaiseHandState(id: Long, isHandRaised: Boolean) {
        participantsList.find {
            it.userId == id
        }?.let { participant ->
            if (isHandRaised) {
                participant.timeHandRaised = System.currentTimeMillis()
                raisedHandsList.add(id)
            } else {
                participant.timeHandRaised = 0L
                raisedHandsList.remove(id)
            }
            anyHandsRaisedState.value = raisedHandsList.isNotEmpty()
            participantChanged.value = participant
        }
    }

    private fun updateAllHandsDown() {
        participantsList.forEach { participant ->
            participant.timeHandRaised = 0L
        }

        raisedHandsList.clear()
        anyHandsRaisedState.value = raisedHandsList.isNotEmpty()

        allHandsDownByAdmin.value = null
    }

    override fun onCleared() {
        super.onCleared()
        kmeSdk.roomController.removeListener(participantsHandler)
    }

}
