package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage

class KmeDesktopShareModuleMessage<T : KmeDesktopShareModuleMessage.DesktopSharePayload> :
    KmeMessage<T>() {

    data class DesktopShareInitOnRoomInitPayload(
        @SerializedName("room_id") val roomId: Long? = null,
        @SerializedName("company_id") val companyId: Long? = null
    ) : DesktopSharePayload()

    data class DesktopShareStateUpdatedPayload(
        @SerializedName("is_active") val isActive: Boolean? = null,
        @SerializedName("on_room_init") val onRoomInit: Boolean? = null,
        @SerializedName("user_id") val userId: Long? = null,
        @SerializedName("user_name") val userName: String? = null,
    ) : DesktopSharePayload()

    data class DesktopShareQualityUpdatedPayload(
        @SerializedName("isHD") val isHD: Boolean? = null
    ) : DesktopSharePayload()

    open class DesktopSharePayload : Payload()

}
