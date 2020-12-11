package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage

class KmeDesktopShareModuleMessage<T : KmeDesktopShareModuleMessage.DesktopSharePayload> : KmeMessage<T>() {

    open class DesktopShareStateUpdatedPayload : DesktopSharePayload() {
        @SerializedName("is_active") val isActive: Boolean? = null
        @SerializedName("user_id") val userId: Long? = null
        @SerializedName("user_name") val userName: String? = null
    }

    open class DesktopShareQualityUpdatedPayload : DesktopSharePayload() {
        @SerializedName("isHD") val isHD: Boolean? = null
    }

    open class DesktopSharePayload : Payload()

}
