package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeXlRoomStatus

class KmeXLRoomModuleMessage<T : KmeXLRoomModuleMessage.XLRoomPayload> : KmeMessage<T>() {

    class XLRoomInitPayload : XLRoomPayload()

    class XLRoomReadyPayload : XLRoomPayload()

    class XLRoomFinishedPayload : XLRoomPayload()

    class XLRoomStatePayload : XLRoomPayload()

    class XLRoomGetStatePayload(
        @SerializedName("room_id") val roomId: Long,
        @SerializedName("company_id") val companyId: Long
    ) : XLRoomPayload()

    open class XLRoomPayload : Payload() {
        @SerializedName("XLRoomStatus")
        val status: KmeXlRoomStatus? = null
    }

}
