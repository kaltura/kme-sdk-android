package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeRoomExitReason
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionModule
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

class KmeRoomSettingsModuleMessage<T : KmeRoomSettingsModuleMessage.SettingsPayload> :
    KmeMessage<T>() {

    data class RoomModuleSettingsChangedPayload(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("permissionsValue") var permissionsValue: KmePermissionValue? = null,
        @SerializedName("moduleName") var moduleName: KmePermissionModule? = null,
        @SerializedName("permissionsKey") var permissionsKey: KmePermissionKey? = null
    ) : SettingsPayload()

    data class UserLeaveSessionPayload(
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("reason") var reason: KmeRoomExitReason? = null
    ) : SettingsPayload()

    data class HostEndSessionPayload(
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("reason") var reason: KmeRoomExitReason? = null
    ) : SettingsPayload()

    data class RoomSettingsChangedPayload(
        @SerializedName("user_id") var userId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("roomSettingValue") var roomSettingValue: KmePermissionValue? = null,
        @SerializedName("changedRoomSetting") var changedRoomSetting: KmePermissionKey? = null
    ) : SettingsPayload()

    open class SettingsPayload : Payload()

}