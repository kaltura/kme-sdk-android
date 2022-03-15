package com.kme.kaltura.kmesdk.util.messages

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEventType
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.KmeRoomExitReason
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeConstraint
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionModule
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

internal fun buildEndSessionMessage(
    roomId: Long,
    companyId: Long,
): KmeRoomSettingsModuleMessage<UserLeaveSessionPayload> {
    return KmeRoomSettingsModuleMessage<UserLeaveSessionPayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        module = KmeMessageModule.ROOM_SETTINGS
        name = KmeMessageEvent.USER_LEAVE_SESSION
        type = KmeMessageEventType.VOID
        payload = UserLeaveSessionPayload(
            roomId,
            companyId,
            KmeRoomExitReason.USER_LEAVE_SESSION
        )
    }
}

internal fun buildRoomSettingsChangedMessage(
    roomId: Long,
    userId: Long,
    key: KmePermissionKey,
    value: KmePermissionValue
): KmeRoomSettingsModuleMessage<RoomSettingsChangedPayload> {
    return KmeRoomSettingsModuleMessage<RoomSettingsChangedPayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        module = KmeMessageModule.ROOM_SETTINGS
        name = KmeMessageEvent.ROOM_SETTINGS_CHANGED
        type = KmeMessageEventType.VOID
        payload = RoomSettingsChangedPayload(
            userId,
            roomId,
            value,
            key
        )
    }
}

internal fun buildPublicChatStateChangedMessage(
    roomId: Long,
    userId: Long,
    key: KmePermissionKey,
    value: KmePermissionValue
): KmeRoomSettingsModuleMessage<RoomChatSettingsChangedPayload> {
    return KmeRoomSettingsModuleMessage<RoomChatSettingsChangedPayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        module = KmeMessageModule.ROOM_SETTINGS
        name = KmeMessageEvent.ROOM_DEFAULT_SETTINGS_CHANGED
        type = KmeMessageEventType.VOID
        payload = RoomChatSettingsChangedPayload().apply {
            this.userId = userId
            this.roomId = roomId
            this.permissionsValue = value
            this.moduleName = KmePermissionModule.CHAT_MODULE
            this.permissionsKey = key
        }
    }
}

internal fun buildEndSessionForEveryoneMessage(
    roomId: Long,
    companyId: Long,
): KmeRoomSettingsModuleMessage<HostEndSessionPayload> {
    return KmeRoomSettingsModuleMessage<HostEndSessionPayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        module = KmeMessageModule.ROOM_SETTINGS
        name = KmeMessageEvent.FORCE_SESSION_END
        type = KmeMessageEventType.VOID
        payload = HostEndSessionPayload(
            roomId,
            companyId,
            KmeRoomExitReason.HOST_ENDED_SESSION
        )
    }
}
