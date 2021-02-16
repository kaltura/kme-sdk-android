package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserType
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardActionType
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardBackgroundType
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardToolType
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath

class KmeWhiteboardModuleMessage<T : KmeWhiteboardModuleMessage.WhiteboardPayload> :
    KmeMessage<T>() {

    data class WhiteboardPageDataPayload(
        @SerializedName("page_drawings") val drawings: List<Drawing>? = null
    ) : WhiteboardPayload()

    data class WhiteboardPageClearedPayload(
        @SerializedName("user_id") internal val drawingUserId: String? = null
    ) : WhiteboardPayload()

    data class ReceiveDrawingPayload(
        @SerializedName("layer") internal val drawingLayer: String? = null,
        @SerializedName("type") internal val drawingType: KmeWhiteboardActionType? = null,
        @SerializedName("tool") internal val drawingTool: KmeWhiteboardToolType? = null,
        @SerializedName("user_id") internal val drawingUserId: String? = null,
        @SerializedName("user_type") internal val drawingUserType: String? = null,
        @SerializedName("path") internal val drawingPath: KmeWhiteboardPath? = null,
        @SerializedName("date_created") internal val drawingCreatedDate: String? = null,
        @SerializedName("user_full_name") internal val drawingFullUsername: String? = null,
    ) : WhiteboardPayload() {
        var drawing: Drawing? = null
    }

    data class DeleteDrawingPayload(
        @SerializedName("user_id") val userId: String? = null,
        @SerializedName("layer") val layer: String? = null,
        @SerializedName("tool") val tool: KmeWhiteboardToolType? = null
    ) : WhiteboardPayload()

    data class BackgroundTypeChangedPayload(
        @SerializedName("user_id") val userId: String? = null,
        @SerializedName("background_type") val backgroundType: KmeWhiteboardBackgroundType? = null
    ) : WhiteboardPayload()

    data class ReceivedLaserPositionPayload(
        @SerializedName("laser_x") val laserX: Float,
        @SerializedName("laser_y") val laserY: Float,
        @SerializedName("user_id") val userId: String? = null,
        @SerializedName("user_type") val userType: KmeUserType? = null
    ) : WhiteboardPayload()

    data class LaserDeactivatedPayload(
        @SerializedName("user_id") val userId: String? = null
    ) : WhiteboardPayload()

    data class SetActivePagePayload(
        @SerializedName("active_page") val activePageId: String? = null,
    ) : WhiteboardPayload()

    data class PageCreatedPayload(
        @SerializedName("id") val id: String? = null,
        @SerializedName("thumbnail") val thumbnail: String? = null,
    ) : WhiteboardPayload()

    open class WhiteboardPayload : Payload() {

        data class Drawing(
            @SerializedName("layer") var layer: String? = null,
            @SerializedName("type") var type: KmeWhiteboardActionType? = null,
            @SerializedName("tool") var tool: KmeWhiteboardToolType? = null,
            @SerializedName("user_id") var userId: String? = null,
            @SerializedName("user_type") var userType: String? = null,
            @SerializedName("board_id") var boardId: String? = null,
            @SerializedName("page_id") var pageId: String? = null,
            @SerializedName("path") var path: KmeWhiteboardPath? = null,
            @SerializedName("date_created") var createdDate: String? = null,
            @SerializedName("user_full_name") var fullUsername: String? = null,
        )

        @SerializedName("board_id")
        val boardId: String? = null

        @SerializedName("page_id")
        val pageId: String? = null
    }

}
