package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType

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
        @SerializedName("user_name") val userName: String? = null
    ) : DesktopSharePayload()

    data class StartDesktopSharePayload(
        @SerializedName("metadata") val metadata: DesktopShareMetadata? = null,
        @SerializedName("content_type") val contentType: KmeContentType? = null
    ) : DesktopSharePayload()

    data class DesktopShareMetadata(
        @SerializedName("user_id") val userId: Long? = null,
        @SerializedName("isConfViewSet") val isConfViewSet: Boolean? = null
    )

    data class UpdateDesktopShareStatePayload(
        @SerializedName("room_id") val roomId: Long? = null,
        @SerializedName("company_id") val companyId: Long? = null,
        @SerializedName("user_id") val userId: String? = null,
        @SerializedName("is_active") val isActive: Boolean? = null
    ) : DesktopSharePayload()

    data class DesktopShareQualityUpdatedPayload(
        @SerializedName("isHD") val isHD: Boolean? = null
    ) : DesktopSharePayload()

    open class DesktopSharePayload : Payload()

}
