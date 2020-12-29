package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardActionType
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardToolType
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath

class KmeWhiteboardModuleMessage<T : KmeWhiteboardModuleMessage.WhiteboardPayload> :
    KmeMessage<T>() {

    data class WhiteboardPageDataPayload(
        @SerializedName("page_drawings") val drawings: List<Drawing>? = null
    ) : WhiteboardPayload()

    open class WhiteboardPayload : Payload() {

        data class Drawing(
            @SerializedName("layer") val layer: String? = null,
            @SerializedName("type") val type: KmeWhiteboardActionType? = null,
            @SerializedName("tool") val tool: KmeWhiteboardToolType? = null,
            @SerializedName("user_id") val userId: String? = null,
            @SerializedName("user_type") val userType: String? = null,
            @SerializedName("board_id") val boardId: String? = null,
            @SerializedName("page_id") val pageId: String? = null,
            @SerializedName("path") val path: KmeWhiteboardPath? = null,
            @SerializedName("date_created") val createdDate: String? = null,
            @SerializedName("user_full_name") val fullUsername: String? = null,
        )

        @SerializedName("board_id")
        val boardId: String? = null

        @SerializedName("page_id")
        val pageId: String? = null
    }

}
