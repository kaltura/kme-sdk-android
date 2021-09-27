package com.kme.kaltura.kmesdk.controller.room.impl

import com.google.gson.Gson
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeParticipantModule
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.room.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.extensions.defineNewDeviceStateByAdmin
import com.kme.kaltura.kmesdk.ifNonNull
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.messages.buildAllHandsDownMessage
import com.kme.kaltura.kmesdk.util.messages.buildChangeMediaStateMessage
import com.kme.kaltura.kmesdk.util.messages.buildRaiseHandMessage
import com.kme.kaltura.kmesdk.util.messages.buildRemoveParticipantMessage
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaStateType
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserRole
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserType
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import org.koin.core.inject
import kotlin.properties.Delegates

class KmeParticipantModuleImpl : KmeController(), IKmeParticipantModule {

    private val roomController: IKmeRoomController by scopedInject()
    private val userController: IKmeUserController by inject()

    private val webSocketModule: IKmeWebSocketModule by scopedInject()

    private var publisherId by Delegates.notNull<Long>()

    private var listener: IKmeParticipantModule.KmeParticipantListener? = null

    private var participants: MutableList<KmeParticipant> = mutableListOf()


    override fun init(listener: IKmeParticipantModule.KmeParticipantListener) {
        this.listener = listener

        roomController.listen(
            roomStateHandler,
            KmeMessageEvent.ROOM_STATE
        )
    }

    override fun subscribe() {
        publisherId = userController.getCurrentUserInfo()?.getUserId() ?: 0
        roomController.listen(
            participantsHandler,
            KmeMessageEvent.USER_MEDIA_STATE_INIT,
            KmeMessageEvent.USER_MEDIA_STATE_CHANGED,
            KmeMessageEvent.PARTICIPANT_MUTED,
            KmeMessageEvent.ALL_PARTICIPANTS_MUTED,
            KmeMessageEvent.USER_STARTED_TO_PUBLISH,
            KmeMessageEvent.NEW_USER_JOINED,
            KmeMessageEvent.ROOM_SETTINGS_CHANGED,
            KmeMessageEvent.MAKE_ALL_USERS_HAND_PUT,
            KmeMessageEvent.USER_HAND_RAISED,
            KmeMessageEvent.USER_REMOVED,
            KmeMessageEvent.USER_DISCONNECTED
        )
    }

    /**
     * get participants list
     */
    override fun participants(): List<KmeParticipant> {
        return participants
    }

    private val roomStateHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.ROOM_STATE -> {
                    val msg: KmeRoomInitModuleMessage<KmeRoomInitModuleMessage.RoomStatePayload>? =
                        message.toType()
                    participants =
                        msg?.payload?.participants?.values?.toMutableList() ?: mutableListOf()

                    participants.filter { participant -> participant.userId ?: 0 < 0 }.forEach {
                        remove(it)
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
                    val msg: KmeRoomInitModuleMessage<KmeRoomInitModuleMessage.NewUserJoinedPayload>? =
                        message.toType()
                    msg?.let {
                        val json = Gson().toJson(it.payload)
                        val participant = Gson().fromJson(json, KmeParticipant::class.java)
                        if (participant != null) {
                            addOrUpdateParticipant(participant)
                        }
                    }
                }
                KmeMessageEvent.USER_MEDIA_STATE_INIT -> {
                    val msg: KmeParticipantsModuleMessage<KmeParticipantsModuleMessage.UserMediaStateInitPayload>? =
                        message.toType()
                    msg?.payload?.let {
                        initUserMediaState(it)
                    }
                }
                KmeMessageEvent.USER_MEDIA_STATE_CHANGED,
                KmeMessageEvent.PARTICIPANT_MUTED -> {
                    val msg: KmeParticipantsModuleMessage<KmeParticipantsModuleMessage.UserMediaStateChangedPayload>? =
                        message.toType()

                    msg?.payload?.let { payload ->
                        ifNonNull(
                            payload.mediaStateType,
                            payload.stateValue
                        ) { stateType, stateValue ->
                            if (payload.userId == publisherId && stateValue != KmeMediaDeviceState.LIVE)
                                listener?.onParticipantMediaStateChanged(
                                    payload.userId!!,
                                    stateType,
                                    stateValue
                                )

                            updateUserMediaState(payload)
                        }
                    }
                }

                KmeMessageEvent.ALL_PARTICIPANTS_MUTED -> {
                    val msg: KmeParticipantsModuleMessage<KmeParticipantsModuleMessage.AllParticipantsMutedPayload>? =
                        message.toType()

                    msg?.payload?.let { payload ->
                        ifNonNull(
                            payload.mediaStateType,
                            payload.stateValue
                        ) { stateType, stateValue ->
                            listener?.onParticipantMediaStateChanged(
                                payload.userId,
                                stateType,
                                stateValue
                            )
                            updateAllMute(payload.userId, stateType)
                        }
                    }
                }
                KmeMessageEvent.USER_STARTED_TO_PUBLISH -> {
                    val msg: KmeStreamingModuleMessage<KmeStreamingModuleMessage.StartedPublishPayload>? =
                        message.toType()
                    msg?.payload?.userId?.toLongOrNull()?.let { updateUserLive(it) }
                }
                KmeMessageEvent.USER_HAND_RAISED -> {
                    val msg: KmeParticipantsModuleMessage<KmeParticipantsModuleMessage.UserRaiseHandPayload>? =
                        message.toType()

                    ifNonNull(
                        msg?.payload?.targetUserId,
                        msg?.payload?.isRaise
                    ) { userId, isRaise ->
                        updateRaiseHandState(userId, isRaise)
                        listener?.onUserHandRaised(userId, isRaise)
                    }
                }
                KmeMessageEvent.MAKE_ALL_USERS_HAND_PUT -> {
                    updateAllHandsDown()
                    listener?.onUpdateAllHandsDown()
                }
                KmeMessageEvent.USER_REMOVED -> {
                    val msg: KmeParticipantsModuleMessage<KmeParticipantsModuleMessage.ParticipantRemovedPayload>? =
                        message.toType()

                    msg?.payload?.targetUserId?.let {
                        remove(it)
                    }
                }
                KmeMessageEvent.USER_DISCONNECTED -> {
                    val userDisconnectedMessage: KmeStreamingModuleMessage<KmeStreamingModuleMessage.UserDisconnectedPayload>? =
                        message.toType()

                    userDisconnectedMessage?.payload?.userId?.let {
                        remove(it)
                    }
                }
                else -> {
                }
            }
        }
    }

    /**
     * get participant with userId
     */
    override fun getParticipant(userId: Long?): KmeParticipant? {
        return participants.find {
            it.userId == userId
        }
    }

    /**
     * add or update participant inside  participants list
     */
    override fun addOrUpdateParticipant(participant: KmeParticipant) {
        getParticipant(participant.userId)?.let { foundParticipant ->
            remove(foundParticipant)
        }

        if (participant.userType == KmeUserType.DIAL) {
            listener?.onDialAdded(participant)
        }

        participants.add(participant)
        listener?.onParticipantChanged(participant)
    }

    /**
     * initialize participant media state
     */
    override fun initUserMediaState(payload: KmeParticipantsModuleMessage.UserMediaStateInitPayload) {
        getParticipant(payload.userId)?.let { participant ->
            participant.micState = payload.micState
            participant.webcamState = payload.webcamState
            participant.liveMediaState = payload.liveMediaState
            listener?.onParticipantChanged(participant)
        }
    }


    /**
     * update participant media state
     */
    override fun updateUserMediaState(payload: KmeParticipantsModuleMessage.UserMediaStateChangedPayload) {
        getParticipant(payload.userId)?.let { participant ->
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

            listener?.onParticipantMediaStatePayLoadChanged(payload)
            listener?.onParticipantChanged(participant)
        }
    }

    /**
     * mute all participant
     */
    override fun updateAllMute(
        initiatorId: Long,
        stateType: KmeMediaStateType
    ) {
        participants.forEach { participant ->
            if (initiatorId == participant.userId) {
                return@forEach
            }
            when (stateType) {
                KmeMediaStateType.MIC -> {
                    participant.micState = KmeMediaDeviceState.DISABLED_LIVE
                }
                KmeMediaStateType.WEBCAM -> {
                    participant.webcamState = KmeMediaDeviceState.DISABLED_LIVE
                }
                else -> {
                }
            }

            val mediaChangePayload = KmeParticipantsModuleMessage.UserMediaStateChangedPayload()
            mediaChangePayload.userId = participant.userId
            mediaChangePayload.mediaStateType = stateType
            mediaChangePayload.stateValue = KmeMediaDeviceState.DISABLED_LIVE

            listener?.onParticipantMediaStatePayLoadChanged(mediaChangePayload)
            listener?.onParticipantChanged(participant)
        }
    }

    /**
     * update user live state
     */
    override fun updateUserLive(userId: Long) {
        getParticipant(userId)?.let { participant ->
            participant.liveMediaState = KmeMediaDeviceState.LIVE_SUCCESS
            listener?.onParticipantChanged(participant)
        }
    }

    /**
     * update raise hand state
     */
    override fun updateRaiseHandState(userId: Long, isHandRaised: Boolean) {
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
     * update all users hand down
     */
    override fun updateAllHandsDown() {
        participants.forEach { participant ->
            participant.timeHandRaised = 0L
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
    override fun allHandsDown(roomId: Long, companyId: Long) {
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

    override fun updateStrongMuteAllMics(value: KmePermissionValue) {
        participants.forEach { participant ->
            if (!isModerator(participant)) {
                participant.micState = participant.micState?.defineNewDeviceStateByAdmin(value)
                if (participant.micState != KmeMediaDeviceState.LIVE) {
                    participant.isSpeaking = false
                }
            }
        }
    }

    override fun updateStrongMuteAllCams(value: KmePermissionValue) {
        participants.forEach { participant ->
            if (!isModerator(participant)) {
                participant.webcamState =
                    participant.webcamState?.defineNewDeviceStateByAdmin(value)
            }
        }
    }

    override fun updateUserModeratorState(userId: Long, isModerator: Boolean) {
        getParticipant(userId)?.let { participant ->
            participant.isModerator = isModerator
        }
    }

    override fun isModerator(participant: KmeParticipant?): Boolean {
        return participant != null
                && (participant.userRole == KmeUserRole.INSTRUCTOR ||
                participant.userRole == KmeUserRole.ADMIN ||
                participant.userRole == KmeUserRole.OWNER ||
                participant.isModerator == true)
    }

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

    override fun remove(participant: KmeParticipant) {
        participants.remove(participant)
    }

    override fun remove(userId: Long) {
        val isRemoved = participants.removeAll { tmp -> tmp.userId == userId }
        listener?.onParticipantRemoved(userId, isRemoved)
    }
}
