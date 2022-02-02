package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionModule
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

class KmeParticipantSettingsModuleMessage<T : KmeParticipantSettingsModuleMessage.ParticipantSettingsPayload> :
    KmeMessage<T>() {

    open class ParticipantSettingsChangedPayload(
        @SerializedName("target_user_id") var targetUserId: Long? = null,
        @SerializedName("room_id") var roomId: Long? = null,
        @SerializedName("company_id") var companyId: Long? = null,
        @SerializedName("changedPermissionsModule") var changedPermissionsModule: KmePermissionModule? = null,
        @SerializedName("changedPermissionKey") var changedPermissionKey: KmePermissionKey? = null,
        @SerializedName("changedPermissionValue") var changedPermissionValue: KmePermissionValue? = null,
        ) : ParticipantSettingsPayload()

    open class ParticipantSettingsPayload : Payload()

}