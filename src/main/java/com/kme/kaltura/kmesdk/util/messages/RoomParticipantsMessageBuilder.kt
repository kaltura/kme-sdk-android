package com.kme.kaltura.kmesdk.util.messages

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEventType
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeConstraint
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaStateType

internal fun buildMediaInitMessage(
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

internal fun buildChangeFocusMessage(
    roomId: Long,
    userId: Long,
    companyId: Long
): KmeParticipantsModuleMessage<ChangeUserFocusEventPayload> {
    return KmeParticipantsModuleMessage<ChangeUserFocusEventPayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        module = KmeMessageModule.ROOM_PARTICIPANTS
        name = KmeMessageEvent.CHANGE_USER_FOCUS_EVENT
        type = KmeMessageEventType.BROADCAST
        payload = ChangeUserFocusEventPayload(
            userId,
            roomId,
            companyId,
            true,
        )
    }
}

internal fun buildChangeMediaStateMessage(
    roomId: Long,
    userId: Long,
    companyId: Long,
    mediaStateType: KmeMediaStateType,
    stateValue: KmeMediaDeviceState
): KmeParticipantsModuleMessage<UserMediaStateChangedPayload> {
    return KmeParticipantsModuleMessage<UserMediaStateChangedPayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        module = KmeMessageModule.ROOM_PARTICIPANTS
        name = KmeMessageEvent.USER_MEDIA_STATE_CHANGED
        type = KmeMessageEventType.VOID
        payload = UserMediaStateChangedPayload(
            userId,
            roomId,
            companyId,
            mediaStateType,
            stateValue
        )
    }
}

internal fun buildRaiseHandMessage(
    roomId: Long,
    userId: Long,
    companyId: Long,
    isRaise: Boolean,
    targetUserId: Long
): KmeParticipantsModuleMessage<UserRaiseHandPayload> {
    return KmeParticipantsModuleMessage<UserRaiseHandPayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        module = KmeMessageModule.ROOM_PARTICIPANTS
        name = KmeMessageEvent.USER_HAND_RAISED
        type = KmeMessageEventType.VOID
        payload = UserRaiseHandPayload(
            userId,
            roomId,
            companyId,
            isRaise,
            targetUserId
        )
    }
}

internal fun buildAllHandsDownMessage(
    roomId: Long,
    companyId: Long,
): KmeParticipantsModuleMessage<AllUsersHandPutPayload> {
    return KmeParticipantsModuleMessage<AllUsersHandPutPayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        module = KmeMessageModule.ROOM_PARTICIPANTS
        name = KmeMessageEvent.MAKE_ALL_USERS_HAND_PUT
        type = KmeMessageEventType.VOID
        payload = AllUsersHandPutPayload(roomId, companyId)
    }
}
