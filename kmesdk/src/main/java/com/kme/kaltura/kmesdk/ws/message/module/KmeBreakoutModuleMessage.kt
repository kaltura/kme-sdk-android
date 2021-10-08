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

    data class BreakoutRoomState(
        @SerializedName("status") var status: KmeBreakoutRoomStatusType?,
        @SerializedName("breakout_rooms") var breakoutRooms: MutableList<BreakoutRoom> = mutableListOf(),
        @SerializedName("assignments") var assignments: MutableList<BreakoutRoomAssignment> = mutableListOf(),
        @SerializedName("failed_assignments") var failedAssignments: MutableList<BreakoutRoomAssignment> = mutableListOf(),
        @SerializedName("removed_assignments") var removedAssignments: MutableList<BreakoutRoomAssignment> = mutableListOf(),
        @SerializedName("start_time") var startTime: Long?,
        @SerializedName("end_time") var endTime: Long?,
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
    ) : BreakoutPayload()

    data class BreakoutRoom(
        @SerializedName("room_id") val id: Long?,
        @SerializedName("room_alias") val alias: String?,
        @SerializedName("index_id") val index: Long?,
        @SerializedName("name") var name: String?,
        @SerializedName("raised_hand_user_id") var raisedHandUserId: Long?,
        @SerializedName("participants_count") var participantsCount: Int,
    ) : BreakoutPayload()

    data class BreakoutRoomAssignment(
        @SerializedName("user_id") val userId: Long?,
        @SerializedName("breakout_room_id") var breakoutRoomId: Long?,
        @SerializedName("status") var status: KmeBreakoutAssignmentStatusType? = null,
    ) : BreakoutPayload()

    data class BreakoutUserJoinedPayload(
        val eventName_: KmeMessageEvent?,
        val roomId_: Long? = null,
        val companyId_: Long? = null,
        @SerializedName("data") val data: BreakoutEventBaseData?,
    ) : BreakoutEventBasePayload(eventName_, roomId_, companyId_)

    data class BreakoutAssignUserPayload(
        val eventName_: KmeMessageEvent?,
        val roomId_: Long? = null,
        val companyId_: Long? = null,
        @SerializedName("data") val data: BreakoutAssignmentsData?,
    ) : BreakoutEventBasePayload(eventName_, roomId_, companyId_)

    data class BreakoutAssignmentsData(
        @SerializedName("assignments") val assignments: List<BreakoutRoomAssignment>?
    ) : BreakoutPayload()

    open class BreakoutEventBasePayload(
        @SerializedName("eventName") val eventName: KmeMessageEvent? = null,
        @SerializedName("room_id") val roomId: Long? = null,
        @SerializedName("company_id") val companyId: Long? = null,
    ) : BreakoutPayload()

    open class BreakoutEventBaseData(
        @SerializedName("user_id") val userId: String? = null,
        @SerializedName("breakout_room_id") val breakoutRoomId: String? = null,
    ) : BreakoutPayload()

    open class BreakoutPayload : KmeMessage.Payload()

}
