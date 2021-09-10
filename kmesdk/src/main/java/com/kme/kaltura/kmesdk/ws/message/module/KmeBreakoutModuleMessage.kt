package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
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

    data class BreakoutChangeRoomNamePayload(
        @SerializedName("room") val room: BreakoutRoom?,
    ) : BreakoutPayload()

    data class BreakoutExtendTimePayload(
        @SerializedName("start_time") val start: Long?,
        @SerializedName("end_time") val end: Long?,
    ) : BreakoutPayload()


    data class BreakoutRoom(
        @SerializedName("room_id") val id: Long?,
        @SerializedName("room_alias") val alias: String?,
        @SerializedName("index_id") val index: Long?,
        @SerializedName("name") val name: String?,
        @SerializedName("raised_hand_user_id") val raisedHandUserId: Long?,
        @SerializedName("participants_count") val participantsCount: Long?,
    ) : BreakoutPayload()

    data class BreakoutRoomAssignment(
        @SerializedName("user_id") val userId: Long?,
        @SerializedName("breakout_room_id") val breakoutRoomId: Long?,
        @SerializedName("status") val status: KmeBreakoutAssignmentStatusType?,
    ) : BreakoutPayload()

    open class BreakoutPayload : KmeMessage.Payload()

}
