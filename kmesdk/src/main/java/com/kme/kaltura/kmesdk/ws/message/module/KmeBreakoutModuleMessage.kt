package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage.BreakoutPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeBreakoutAssignmentStatusType
import com.kme.kaltura.kmesdk.ws.message.type.KmeBreakoutRoomMessageType
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
        @SerializedName("messageType") val messageType: KmeBreakoutRoomMessageType?,
        @SerializedName("messageMetadata") val messageMetadata: BreakoutMessageMetadata?,
    ) : BreakoutPayload()

    data class BreakoutMessageMetadata(
        @SerializedName("messageText") val messageText: String?,
        @SerializedName("senderId") val senderId: Long?,
        @SerializedName("senderName") val senderName: String?,
        @SerializedName("name") val name: String?,
    ) : BreakoutPayload() {
        //local
        var senderAvatar: String? = null

        override fun equals(other: Any?): Boolean {
            if (javaClass != other?.javaClass) return false

            other as BreakoutMessageMetadata

            if (messageText != other.messageText) return false
            if (senderId != other.senderId) return false
            if (senderName != other.senderName) return false
            if (name != other.name) return false

            return true
        }

        override fun hashCode(): Int {
            var result = messageText?.hashCode() ?: 0
            result = 31 * result + (senderId?.hashCode() ?: 0)
            result = 31 * result + (senderName?.hashCode() ?: 0)
            result = 31 * result + (name?.hashCode() ?: 0)
            return result
        }

    }

    open class BreakoutRoom : BreakoutPayload() {
        @SerializedName("room_id")
        var id: Long? = null

        @SerializedName("room_alias")
        var alias: String? = null

        @SerializedName("index_id")
        var index: Long? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("raised_hand_user_id")
        var raisedHandUserId: Long? = null

        @SerializedName("participants_count")
        var participantsCount: Int = 0

        override fun equals(other: Any?): Boolean {
            if (javaClass != other?.javaClass) return false

            other as BreakoutRoom

            if (id != other.id) return false
            if (alias != other.alias) return false
            if (index != other.index) return false
            if (name != other.name) return false
            if (raisedHandUserId != other.raisedHandUserId) return false
            if (participantsCount != other.participantsCount) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id?.hashCode() ?: 0
            result = 31 * result + (alias?.hashCode() ?: 0)
            result = 31 * result + (index?.hashCode() ?: 0)
            result = 31 * result + (name?.hashCode() ?: 0)
            result = 31 * result + (raisedHandUserId?.hashCode() ?: 0)
            result = 31 * result + participantsCount
            return result
        }

    }

    data class BreakoutRoomAssignment(
        @SerializedName("user_id") val userId: Long?,
        @SerializedName("breakout_room_id") var breakoutRoomId: Long?,
        @SerializedName("status") var status: KmeBreakoutAssignmentStatusType? = null,
    ) : BreakoutPayload()

    data class BreakoutUserJoinedPayload(
        @SerializedName("eventName")
        override val eventName: KmeMessageEvent?,
        @SerializedName("room_id")
        override val roomId: Long? = null,
        @SerializedName("company_id")
        override val companyId: Long? = null,
        @SerializedName("data") val data: BreakoutEventBaseData?,
    ) : BreakoutEventBasePayload()

    data class BreakoutAssignUserPayload(
        @SerializedName("eventName")
        override val eventName: KmeMessageEvent?,
        @SerializedName("room_id")
        override val roomId: Long? = null,
        @SerializedName("company_id")
        override val companyId: Long? = null,
        @SerializedName("data") val data: BreakoutAssignmentsData?,
    ) : BreakoutEventBasePayload()

    data class BreakoutAssignmentsData(
        @SerializedName("assignments") val assignments: List<BreakoutRoomAssignment>?
    ) : BreakoutPayload()

    abstract class BreakoutEventBasePayload : BreakoutPayload() {
        abstract val eventName: KmeMessageEvent?
        abstract val roomId: Long?
        abstract val companyId: Long?
    }

    open class BreakoutEventBaseData(
        @SerializedName("user_id") val userId: String? = null,
        @SerializedName("breakout_room_id") val breakoutRoomId: String? = null,
    ) : BreakoutPayload()

    open class BreakoutPayload : KmeMessage.Payload()

}
