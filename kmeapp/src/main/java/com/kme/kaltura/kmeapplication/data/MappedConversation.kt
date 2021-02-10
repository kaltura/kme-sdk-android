package com.kme.kaltura.kmeapplication.data

import android.os.Parcelable
import com.kme.kaltura.kmesdk.ws.message.type.KmeConversationType
import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.commons.models.IUser
import kotlinx.android.parcel.Parcelize

@Parcelize
class MappedConversation(
    private val conversationId: String,
    private val conversationName: String,
    private val avatar: String?,
    private val participants: MutableList<MappedUser>,
    private var lastChatMessage: MappedChatMessage?,
    var unreadCount: Int?,
    val conversationType: KmeConversationType?,
    var isSystem: Boolean? = null,
    var hasAccess: Boolean? = null
) : IDialog<MappedChatMessage>, Parcelable {

    override fun getId(): String = conversationId

    override fun getDialogPhoto(): String? = avatar

    override fun getDialogName(): String = conversationName

    override fun getUsers(): MutableList<out IUser>? = participants

    override fun getLastMessage(): MappedChatMessage? = lastChatMessage

    override fun setLastMessage(message: MappedChatMessage?) {
        this.lastChatMessage = message
    }

    override fun getUnreadCount(): Int = unreadCount ?: 0
}