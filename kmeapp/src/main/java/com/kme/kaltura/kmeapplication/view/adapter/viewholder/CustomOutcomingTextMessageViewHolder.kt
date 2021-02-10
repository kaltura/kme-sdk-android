package com.kme.kaltura.kmeapplication.view.adapter.viewholder

import android.view.Gravity
import android.view.View
import androidx.core.view.forEach
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.data.MappedChatMessage
import com.kme.kaltura.kmeapplication.util.extensions.gone
import com.kme.kaltura.kmeapplication.util.extensions.isModerator
import com.kme.kaltura.kmeapplication.util.extensions.popup
import com.kme.kaltura.kmeapplication.util.extensions.visible
import com.kme.kaltura.kmesdk.ws.message.type.KmeConversationType
import com.stfalcon.chatkit.messages.MessageHolders.OutcomingTextMessageViewHolder
import com.stfalcon.chatkit.utils.DateFormatter
import kotlinx.android.synthetic.main.item_custom_outcoming_text_message.view.*
import kotlinx.android.synthetic.main.item_reply_text_message.view.*

class CustomOutcomingTextMessageViewHolder(
    itemView: View,
    payloadData: Any
) : OutcomingTextMessageViewHolder<MappedChatMessage>(itemView, payloadData) {

    private val outcomingPayload = payload as Payload

    override fun onBind(message: MappedChatMessage?) {
        super.onBind(message)
        if (message != null) {
            with(itemView) {
                when (message.status) {
//                    SENDING -> {
//                        messageStatus.visible()
//                        messageStatus.text = context.getString(R.string.sending)
//                    }
//                    SENT -> {
//                        messageStatus.visible()
//                        messageStatus.text = context.getString(R.string.sent)
//                    }
//                    ERROR -> {
//                        messageStatus.visible()
//                        messageStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
//                        messageStatus.text = context.getString(R.string.failed)
//                    }
//                    null -> {
//                        messageStatus.gone()
//                    }
                }

                setOnClickListener {
                    val currentParticipant =
                        outcomingPayload.contextMenuListener?.getCurrentParticipant()
                    val isModerator = currentParticipant.isModerator()
                    val isQna = KmeConversationType.QNA == outcomingPayload.chatType
                    it.bubble.popup(R.menu.menu_chat_overflow, true, Gravity.START).apply {
                        menu.forEach { menuItem ->
                            when (menuItem.itemId) {
                                R.id.action_delete -> menuItem.isVisible = isModerator
                                R.id.action_reply -> menuItem.isVisible = !isQna
                                R.id.action_private_chat -> menuItem.isVisible = false
                            }
                        }

                        setOnMenuItemClickListener { menuItem ->
                            when (menuItem?.itemId) {
                                R.id.action_reply -> {
                                    outcomingPayload.contextMenuListener?.onReply(message)
                                }
                                R.id.action_delete -> {
                                    outcomingPayload.contextMenuListener?.onDelete(message)
                                }
                            }
                            return@setOnMenuItemClickListener true
                        }
                        show()
                    }
                }

                message.replyMessage?.let {
                    if (it.id.isEmpty()) {
                        replyMessageLayout.gone()
                    } else {
                        showReplyMessage(this, it)
                    }
                } ?: run {
                    replyMessageLayout.gone()
                }


                when (message.styleType) {
                    MappedChatMessage.StyleType.START_MESSAGE_STYLE -> {
                        messageTime.visible()
                        bubble.setBackgroundResource(R.drawable.bg_start_outcoming_message)
                    }
                    MappedChatMessage.StyleType.MIDDLE_MESSAGE_STYLE -> {
                        messageTime.gone()
                        bubble.setBackgroundResource(R.drawable.bg_middle_outcoming_message)
                    }
                    MappedChatMessage.StyleType.END_MESSAGE_STYLE -> {
                        messageTime.gone()
                        bubble.setBackgroundResource(R.drawable.bg_end_outcoming_message)
                    }
                }
            }
        }
    }

    private fun showReplyMessage(itemView: View, replyMessage: MappedChatMessage) {
        itemView.replyMessageLayout.visible()
        itemView.replyMessageLayout.replyUserName.text = replyMessage.user.name
        itemView.replyMessageLayout.replyMessageText.text = replyMessage.text
        itemView.replyMessageLayout.replyMessageTime.text = if (replyMessage.createdAt.time == 0L)
            ""
        else
            DateFormatter.format(replyMessage.createdAt, DateFormatter.Template.TIME)
    }

    class Payload {
        var chatType: KmeConversationType? = null
        var contextMenuListener: OnChatContextMenuListener? = null
    }

}