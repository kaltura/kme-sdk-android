package com.kme.kaltura.kmeapplication.data

import android.os.Parcelable
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserInfoData
import com.kme.kaltura.kmesdk.ws.message.chat.KmeChatMessage
import com.stfalcon.chatkit.commons.models.IMessage
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class MappedChatMessage(
    private val user: MappedUser,
    private val messageId: String,
    private val message: String,
    private val createdAt: Date,
    var conversationId: String,
    var status: ChatMessageStatus? = null,
    var replyMessage: MappedChatMessage? = null,
    var replyAll: Boolean = false,
    var styleType: StyleType = StyleType.END_MESSAGE_STYLE
) : IMessage, Parcelable {

    override fun getId() = messageId

    override fun getText() = message

    override fun getUser() = user

    override fun getCreatedAt() = createdAt

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MappedChatMessage

        if (messageId != other.messageId) return false
        if (conversationId != other.conversationId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = messageId.hashCode()
        result = 31 * result + conversationId.hashCode()
        return result
    }

    enum class StyleType {
        START_MESSAGE_STYLE,
        MIDDLE_MESSAGE_STYLE,
        END_MESSAGE_STYLE
    }

    fun asKmeMessage() : KmeChatMessage {
        val id = messageId
        val message = message
        val replyAll = replyAll
        val replyMessage = replyMessage
        return KmeChatMessage().apply {
            this.id = id
            this.message = message
            this.replyAll = if (replyAll) replyAll else null
            this.user = KmeUserInfoData().apply {
                this.id = replyMessage?.user?.id?.toLong()
                this.fullName = replyMessage?.user?.name
                this.avatar = replyMessage?.user?.avatar
            }
        }
    }

}
