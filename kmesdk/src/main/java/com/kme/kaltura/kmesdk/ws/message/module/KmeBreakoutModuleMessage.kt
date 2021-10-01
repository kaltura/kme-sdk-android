package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage.BreakoutPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeBreakoutAssignmentStatusType
import com.kme.kaltura.kmesdk.ws.message.type.KmeBreakoutRoomStatusType

class KmeBreakoutModuleMessage<T : BreakoutPayload> : KmeMessage<T>() {

    data class GetBreakoutStatePayload(
        @SerializedName("room_id") val roomId: Long,
        @SerializedName("company_id") val companyId: Long,
    ) : BreakoutPayload()

    data class BreakoutRoomStatusPayload(
        @SerializedName("status") val status: KmeBreakoutRoomStatusType?,
        @SerializedName("breakout_rooms") val breakoutRooms: List<BreakoutRoom>?,
        @SerializedName("assignments") val assignments: List<BreakoutRoomAssignment>?,
        @SerializedName("failed_assignments") val failedAssignments: List<BreakoutRoomAssignment>?,
        @SerializedName("removed_assignments") val removedAssignments: List<BreakoutRoomAssignment>?,
        @SerializedName("start_time") val startTime: Long?,
        @SerializedName("end_time") val endTime: Long?,
    ) : BreakoutPayload()

    data class BreakoutAddRoomPayload(
        @SerializedName("room") val room: BreakoutRoom?,
    ) : BreakoutPayload()

    data class BreakoutChangeNamePayload(
        @SerializedName("room") val room: BreakoutRoom?,
    ) : BreakoutPayload()

    data class BreakoutExtendTimePayload(
        @SerializedName("start_time") val start: Long?,
        @SerializedName("end_time") val end: Long?,
    ) : BreakoutPayload()

    data class BreakoutMessagePayload(
        @SerializedName("messageType") val messageType: String?,
        @SerializedName("messageMetadata") val messageMetadata: BreakoutMessageMetadata?,
    ) : BreakoutPayload()

    data class BreakoutMessageMetadata(
        @SerializedName("messageText") val messageText: String?,
        @SerializedName("senderId") val senderId: Long?,
        @SerializedName("senderName") val senderName: String?,
        @SerializedName("name") val name: String?,
    )

    data class BreakoutRoom(
        @SerializedName("room_id") val id: Long?,
        @SerializedName("room_alias") val alias: String?,
        @SerializedName("index_id") val index: Long?,
        @SerializedName("name") var name: String?,
        @SerializedName("raised_hand_user_id") val raisedHandUserId: Long?,
        @SerializedName("participants_count") val participantsCount: Long?,
    ) : BreakoutPayload()

    data class BreakoutRoomAssignment(
        @SerializedName("user_id") val userId: Long?,
        @SerializedName("breakout_room_id") val breakoutRoomId: Long?,
        @SerializedName("status") val status: KmeBreakoutAssignmentStatusType?,
    ) : BreakoutPayload()


    data class BreakoutUserJoinedData(
        @SerializedName("user_id") val userId: String?,
        @SerializedName("breakout_room_id") val breakoutRoomId: String?,
    ) : BreakoutPayload()

    data class BreakoutUserJoinedPayload(
        @SerializedName("event_name") val eventName: KmeMessageEvent?,
        @SerializedName("data") val data: BreakoutUserJoinedData?,
        @SerializedName("room_id") val roomId: Long?,
        @SerializedName("company_id") val companyId: Long?,
    ) : BreakoutPayload()

    open class BreakoutPayload : KmeMessage.Payload()

}
