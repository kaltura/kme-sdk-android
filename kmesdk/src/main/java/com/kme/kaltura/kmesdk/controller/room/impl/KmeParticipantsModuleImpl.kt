package com.kme.kaltura.kmesdk.controller.room.impl

import com.google.gson.Gson
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeParticipantModule
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.room.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.controller.room.internal.IKmeParticipantsInternalModule
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.ifNonNull
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.extensions.defineNewDeviceStateByAdmin
import com.kme.kaltura.kmesdk.util.extensions.isModerator
import com.kme.kaltura.kmesdk.util.messages.buildAllHandsDownMessage
import com.kme.kaltura.kmesdk.util.messages.buildChangeMediaStateMessage
import com.kme.kaltura.kmesdk.util.messages.buildRaiseHandMessage
import com.kme.kaltura.kmesdk.util.messages.buildRemoveParticipantMessage
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaStateType
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserType
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import org.koin.core.inject

class KmeParticipantsModuleImpl : KmeController(), IKmeParticipantsInternalModule {

    private val roomController: IKmeRoomController by scopedInject()
    private val webSocketModule: IKmeWebSocketModule by scopedInject()
    private val userController: IKmeUserController by inject()

    private val publisherId by lazy { userController.getCurrentUserInfo()?.getUserId() ?: 0 }
    private var listener: IKmeParticipantModule.KmeParticipantListener? = null
    private var participants: MutableList<KmeParticipant> = mutableListOf()

    /**
     * subscribe roomStateHandler to get participants list
     */
    override fun init(listener: IKmeParticipantModule.KmeParticipantListener) {
        this.listener = listener

        roomController.listen(
            roomStateHandler,
            KmeMessageEvent.ROOM_STATE
        )
    }

    /**
     * Subscribing for the room events related to participants
     */
    override fun subscribe() {
        roomController.listen(
            participantsHandler,
            KmeMessageEvent.USER_MEDIA_STATE_INIT,
            KmeMessageEvent.USER_MEDIA_STATE_CHANGED,
            KmeMessageEvent.PARTICIPANT_MUTED,
            KmeMessageEvent.ALL_PARTICIPANTS_MUTED,
            KmeMessageEvent.USER_STARTED_TO_PUBLISH,
            KmeMessageEvent.NEW_USER_JOINED,
            KmeMessageEvent.SET_PARTICIPANT_MODERATOR,
            KmeMessageEvent.ROOM_SETTINGS_CHANGED,
            KmeMessageEvent.MAKE_ALL_USERS_HAND_PUT,
            KmeMessageEvent.USER_HAND_RAISED,
            KmeMessageEvent.USER_REMOVED,
            KmeMessageEvent.USER_DISCONNECTED
        )
    }

    /**
     * Get participants list
     */
    override fun participants() = participants

    private val roomStateHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.ROOM_STATE -> {
                    val msg: KmeRoomInitModuleMessage<RoomStatePayload>? = message.toType()
                    participants =
                        msg?.payload?.participants?.values?.toMutableList() ?: mutableListOf()

                    participants.filter { participant ->
                        participant.userId ?: 0 < 0
                    }.forEach {
                        participants.remove(it)
                    }

                    listener?.onParticipantsLoaded(participants)
                }
            }
        }
    }

    private val participantsHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.NEW_USER_JOINED -> {
                    val msg: KmeRoomInitModuleMessage<NewUserJoinedPayload>? = message.toType()
                    msg?.let {
                        val json = Gson().toJson(it.payload)
                        val participant = Gson().fromJson(json, KmeParticipant::class.java)
                        if (participant != null) {
                            addOrUpdateParticipant(participant)
                        }
                    }
                }
                KmeMessageEvent.SET_PARTICIPANT_MODERATOR -> {
                    val settingsMessage: KmeParticipantsModuleMessage<SetParticipantModerator>? =
                        message.toType()

                    ifNonNull(
                        settingsMessage?.payload?.targetUserId,
                        settingsMessage?.payload?.isModerator
                    ) { userId, isModerator ->
                        updateUserModeratorState(userId, isModerator)
                    }
                }
                KmeMessageEvent.USER_MEDIA_STATE_INIT -> {
                    val msg: KmeParticipantsModuleMessage<UserMediaStateInitPayload>? =
                        message.toType()
                    msg?.payload?.let {
                        initUserMediaState(it)
                    }
                }
                KmeMessageEvent.USER_MEDIA_STATE_CHANGED,
                KmeMessageEvent.PARTICIPANT_MUTED -> {
                    val msg: KmeParticipantsModuleMessage<UserMediaStateChangedPayload>? =
                        message.toType()
                    msg?.payload?.let { payload ->
                        ifNonNull(
                            payload.mediaStateType,
                            payload.stateValue
                        ) { stateType, stateValue ->
                            if (payload.userId == publisherId && stateValue != KmeMediaDeviceState.LIVE)
                                getParticipant(payload.userId)?.let { participant ->
                                    listener?.onParticipantMediaStateChanged(
                                        participant,
                                        stateType,
                                        stateValue
                                    )
                                }
                            updateUserMediaState(payload)
                        }
                    }
                }
                KmeMessageEvent.ALL_PARTICIPANTS_MUTED -> {
                    val msg: KmeParticipantsModuleMessage<AllParticipantsMutedPayload>? =
                        message.toType()
                    msg?.payload?.let { payload ->
                        updateAllMute(payload)
                    }
                }
                KmeMessageEvent.USER_STARTED_TO_PUBLISH -> {
                    val msg: KmeStreamingModuleMessage<StartedPublishPayload>? = message.toType()
                    msg?.payload?.userId?.toLongOrNull()?.let {
                        userLive(it)
                    }
                }
                KmeMessageEvent.USER_HAND_RAISED -> {
                    val msg: KmeParticipantsModuleMessage<UserRaiseHandPayload>? = message.toType()
                    ifNonNull(
                        msg?.payload?.targetUserId,
                        msg?.payload?.isRaise
                    ) { userId, isRaise ->
                        updateRaiseHandState(userId, isRaise)
                        listener?.onUserHandRaised(userId, isRaise)
                    }
                }
                KmeMessageEvent.MAKE_ALL_USERS_HAND_PUT -> {
                    allHandsDown()
                    listener?.onUpdateAllHandsDown()
                }
                KmeMessageEvent.USER_REMOVED -> {
                    val msg: KmeParticipantsModuleMessage<ParticipantRemovedPayload>? =
                        message.toType()
                    msg?.payload?.targetUserId?.let {
                        remove(it)
                    }
                }
                KmeMessageEvent.USER_DISCONNECTED -> {
                    val msg: KmeStreamingModuleMessage<UserDisconnectedPayload>? = message.toType()
                    msg?.payload?.userId?.let {
                        remove(it)
                    }
                }
                else -> {
                }
            }
        }
    }

    /**
     * Get participant with userId
     */
    override fun getParticipant(userId: Long?) = participants.find {
        it.userId == userId
    }

    /**
     * User live media state
     */
    override fun userLive(userId: Long) {
        getParticipant(userId)?.let { participant ->
            participant.liveMediaState = KmeMediaDeviceState.LIVE_SUCCESS
            listener?.onParticipantChanged(participant)
        }
    }

    /**
     * Raise participant hand
     */
    override fun raiseHand(
        roomId: Long,
        companyId: Long,
        userId: Long,
        targetUserId: Long,
        isRaise: Boolean
    ) {
        webSocketModule.send(
            buildRaiseHandMessage(
                roomId,
                companyId,
                userId,
                targetUserId,
                isRaise
            )
        )
    }

    /**
     * Put all hands down in the room
     */
    override fun allHandsDown(
        roomId: Long,
        companyId: Long
    ) {
        webSocketModule.send(buildAllHandsDownMessage(roomId, companyId))
    }

    /**
     * Changes media state of participant in the room
     */
    override fun changeMediaState(
        roomId: Long,
        companyId: Long,
        userId: Long,
        mediaStateType: KmeMediaStateType,
        stateValue: KmeMediaDeviceState
    ) {
        webSocketModule.send(
            buildChangeMediaStateMessage(
                roomId,
                companyId,
                userId,
                mediaStateType,
                stateValue
            )
        )
    }

    /**
     * Mute all users mics
     */
    override fun muteAllMics(value: KmePermissionValue) {
        participants.forEach { participant ->
            if (!participant.isModerator()) {
                participant.micState = participant.micState?.defineNewDeviceStateByAdmin(value)
                if (participant.micState != KmeMediaDeviceState.LIVE) {
                    participant.isSpeaking = false
                }
            }
        }
    }

    /**
     * Mute all users cams
     */
    override fun muteAllCams(value: KmePermissionValue) {
        participants.forEach { participant ->
            if (!participant.isModerator()) {
                participant.webcamState =
                    participant.webcamState?.defineNewDeviceStateByAdmin(value)
            }
        }
    }

    /**
     * Removes participant from the room
     */
    override fun remove(
        roomId: Long,
        companyId: Long,
        userId: Long,
        targetId: Long
    ) {
        webSocketModule.send(
            buildRemoveParticipantMessage(
                roomId,
                companyId,
                userId,
                targetId
            )
        )
    }

    override fun participantSpeaking(
        id: Long,
        isSpeaking: Boolean
    ) {
        getParticipant(id)?.let {
            it.isSpeaking = isSpeaking
            it.lastSpeakingTime = System.currentTimeMillis()
        }
    }

    /**
     * Add or update participants list
     */
    private fun addOrUpdateParticipant(participant: KmeParticipant) {
        getParticipant(participant.userId)?.let { foundParticipant ->
            participants.remove(foundParticipant)
        }

        if (participant.userType == KmeUserType.DIAL) {
            listener?.onDialAdded(participant)
        }

        participants.add(participant)
        listener?.onParticipantChanged(participant)
    }

    /**
     * Initialize user media state
     */
    private fun initUserMediaState(payload: UserMediaStateInitPayload) {
        getParticipant(payload.userId)?.let { participant ->
            participant.micState = payload.micState
            participant.webcamState = payload.webcamState
            participant.liveMediaState = payload.liveMediaState
            listener?.onParticipantChanged(participant)
        }
    }

    /**
     * Update user media state
     */
    private fun updateUserMediaState(payload: UserMediaStateChangedPayload) {
        ifNonNull(
            payload.mediaStateType,
            payload.stateValue
        ) { stateType, stateValue ->

            getParticipant(payload.userId)?.let { participant ->
                when (stateType) {
                    KmeMediaStateType.MIC -> {
                        participant.micState = stateValue
                        if (stateValue != KmeMediaDeviceState.LIVE) {
                            participant.isSpeaking = false
                        }
                    }
                    KmeMediaStateType.WEBCAM -> {
                        participant.webcamState = stateValue
                    }
                    KmeMediaStateType.LIVE_MEDIA -> {
                        participant.liveMediaState = stateValue
                    }
                }

//                if (payload.userId == publisherId) {
//                    if (stateValue != KmeMediaDeviceState.LIVE) {
//                        listener?.onPublisherMediaStateChanged(
//                            stateType,
//                            stateValue
//                        )
//                    }
//                } else {
//                    listener?.onParticipantMediaStateChanged(
//                        participant,
//                        stateType,
//                        stateValue
//                    )
//                }

                listener?.onParticipantMediaStateChanged(
                    participant,
                    stateType,
                    stateValue
                )
                listener?.onParticipantChanged(participant)
            }
        }
    }

    /**
     * Mute all participant
     */
    private fun updateAllMute(payload: AllParticipantsMutedPayload) {
        ifNonNull(
            payload.mediaStateType,
            payload.stateValue
        ) { stateType, stateValue ->
            participants.forEach { participant ->
                if (payload.userId == participant.userId) {
                    return@forEach
                }
                when (stateType) {
                    KmeMediaStateType.MIC -> {
                        participant.micState = stateValue
                    }
                    KmeMediaStateType.WEBCAM -> {
                        participant.webcamState = stateValue
                    }
                    else -> {
                    }
                }

                if (payload.userId == publisherId) {
                    if (stateValue != KmeMediaDeviceState.LIVE) {
                        listener?.onPublisherMediaStateChanged(
                            stateType,
                            stateValue
                        )
                    }
                } else {
                    listener?.onParticipantMediaStateChanged(
                        participant,
                        stateType,
                        stateValue
                    )
                }
                listener?.onParticipantChanged(participant)
            }
        }
    }

    /**
     * Update user raise state
     */
    private fun updateRaiseHandState(
        userId: Long,
        isHandRaised: Boolean
    ) {
        getParticipant(userId)?.let { participant ->
            if (isHandRaised) {
                participant.timeHandRaised = System.currentTimeMillis()
            } else {
                participant.timeHandRaised = 0L
            }
            listener?.onParticipantChanged(participant)
        }
    }

    /**
     * All users hand down
     */
    private fun allHandsDown() {
        participants.forEach { participant ->
            participant.timeHandRaised = 0L
        }
    }

    /**
     * The user role has been modified by moderator
     */
    private fun updateUserModeratorState(
        userId: Long,
        isModerator: Boolean
    ) {
        getParticipant(userId)?.let { participant ->
            participant.isModerator = isModerator
        }
    }

    /**
     * Remove user from list with an userId
     */
    private fun remove(userId: Long) {
        val isRemoved = participants.removeAll { tmp ->
            tmp.userId == userId
        }
        listener?.onParticipantRemoved(userId, isRemoved)
    }

}
