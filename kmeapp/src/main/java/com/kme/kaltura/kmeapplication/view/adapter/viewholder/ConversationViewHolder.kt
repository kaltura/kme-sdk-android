package com.kme.kaltura.kmeapplication.view.adapter.viewholder

import android.view.View
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.data.MappedConversation
import com.kme.kaltura.kmeapplication.util.extensions.gone
import com.kme.kaltura.kmeapplication.util.extensions.invisible
import com.kme.kaltura.kmeapplication.util.extensions.visible
import com.kme.kaltura.kmesdk.ws.message.type.KmeConversationType.*
import com.stfalcon.chatkit.dialogs.DialogsListAdapter.DialogViewHolder
import kotlinx.android.synthetic.main.item_conversation_layout.view.*

class ConversationViewHolder(itemView: View) : DialogViewHolder<MappedConversation>(itemView) {

    override fun onBind(dialog: MappedConversation?) {
        super.onBind(dialog)
        dialog?.let {
            bind(it)
        }
    }

    private fun bind(conversation: MappedConversation) {
        with(itemView) {
            when (conversation.conversationType) {
                PUBLIC -> {
                    dialogAvatar.invisible()
                    systemDialogAvatarContainer.visible()
                    systemDialogAvatar.setImageResource(R.drawable.ic_room_admin_chat_off)
                }
                MODERATORS -> {
                    dialogAvatar.invisible()
                    systemDialogAvatarContainer.visible()
                    systemDialogAvatar.setImageResource(R.drawable.ic_moderator)
                }
                QNA -> {
                    dialogAvatar.invisible()
                    systemDialogAvatarContainer.visible()
                    systemDialogAvatar.setImageResource(R.drawable.ic_qna)
                }
                else -> {
                    dialogAvatar.visible()
                    systemDialogAvatarContainer.gone()
                }
            }

            if (conversation.lastMessage == null || conversation.lastMessage?.id.isNullOrEmpty()) {
                dialogLastMessage.gone()
                dialogDate.gone()
            } else {
                dialogLastMessage.visible()
                dialogDate.visible()
            }

            val unreadCount = conversation.unreadCount ?: 0
            if (unreadCount <= 0) {
                dialogUnreadBubble.gone()
            } else {
                dialogUnreadBubble.visible()
            }
        }
    }
}