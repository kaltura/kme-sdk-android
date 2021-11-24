package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageReason
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmeModuleVisibilityValue
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionModule
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

class KmeRoomSettingsModuleMessage<T : KmeRoomSettingsModuleMessage.SettingsPayload> :
    KmeMessage<T>() {

    open class RoomDefaultSettingsChangedPayload(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("moduleName") var moduleName: KmePermissionModule? = null,
        @SerializedName("permissionsKey") var permissionsKey: KmePermissionKey? = null
    ) : SettingsPayload()

    data class RoomChatSettingsChangedPayload(
        @SerializedName("permissionsValue") var permissionsValue: KmePermissionValue? = null,
    ) : RoomDefaultSettingsChangedPayload()

    data class RoomParticipantSettingsChangedPayload(
        @SerializedName("permissionsValue") var permissionsValue: KmeModuleVisibilityValue? = null,
    ) : RoomDefaultSettingsChangedPayload()

    data class UserLeaveSessionPayload(
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("reason") var reason: KmeMessageReason? = null
    ) : SettingsPayload()

    data class HostEndSessionPayload(
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("reason") var reason: KmeMessageReason? = null
    ) : SettingsPayload()

    data class RoomSettingsChangedPayload(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("roomSettingValue") var roomSettingValue: KmePermissionValue? = null,
        @SerializedName("changedRoomSetting") var changedRoomSetting: KmePermissionKey? = null
    ) : SettingsPayload()

    open class SettingsPayload : Payload()

}