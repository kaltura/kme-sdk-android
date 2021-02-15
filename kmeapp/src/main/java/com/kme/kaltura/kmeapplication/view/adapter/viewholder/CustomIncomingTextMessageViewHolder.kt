package com.kme.kaltura.kmeapplication.view.adapter.viewholder

import android.view.View
import androidx.core.view.forEach
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.data.MappedChatMessage
import com.kme.kaltura.kmeapplication.data.MappedChatMessage.StyleType.*
import com.kme.kaltura.kmeapplication.util.extensions.gone
import com.kme.kaltura.kmeapplication.util.extensions.isModerator
import com.kme.kaltura.kmeapplication.util.extensions.popup
import com.kme.kaltura.kmeapplication.util.extensions.visible
import com.kme.kaltura.kmesdk.ws.message.type.KmeConversationType
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue.ON
import com.stfalcon.chatkit.messages.MessageHolders.IncomingTextMessageViewHolder
import com.stfalcon.chatkit.utils.DateFormatter
import kotlinx.android.synthetic.main.item_custom_incoming_text_message.view.*
import kotlinx.android.synthetic.main.item_reply_text_message.view.*

class CustomIncomingTextMessageViewHolder(
    itemView: View,
    private val payloadData: Any
) : IncomingTextMessageViewHolder<MappedChatMessage>(itemView, payloadData) {

    override fun onBind(message: MappedChatMessage?) {
        super.onBind(message)
        if (message != null) {
            with(itemView) {
                setOnClickListener {
                    val payload = (payloadData as Payload)
                    val participant = payload.contextMenuListener?.getCurrentParticipant()

                    val isModerator = participant.isModerator()
                    val isPrivateChatEnable =
                        participant?.userPermissions?.chatModule?.defaultSettings?.startPrivateChat == ON
                                && (payload.contextMenuListener?.isParticipantJoined(message.user.id.toLong())
                            ?: false)

                    val isQnaType = KmeConversationType.QNA == payload.chatType

                    it.bubble.popup(R.menu.menu_chat_overflow, true).apply {
                        menu.forEach { menuItem ->
                            when (menuItem.itemId) {
                                R.id.action_delete -> menuItem.isVisible = isModerator
                                R.id.action_private_chat -> menuItem.isVisible = isPrivateChatEnable
                                R.id.action_reply -> menuItem.isVisible =
                                    !isQnaType || (isModerator && message.replyMessage == null)
                                R.id.action_reply_all -> menuItem.isVisible =
                                    isQnaType && isModerator && message.replyMessage == null
                            }
                        }
                        setOnMenuItemClickListener { menuItem ->
                            when (menuItem?.itemId) {
                                R.id.action_reply -> {
                                    message.replyAll = false
                                    payload.contextMenuListener?.onReply(message)
                                }
                                R.id.action_reply_all -> {
                                    message.replyAll = true
                                    payload.contextMenuListener?.onReply(message)
                                }
                                R.id.action_private_chat -> {
                                    payload.contextMenuListener?.onStartPrivateChat(message.user.id.toLong())
                                }
                                R.id.action_delete -> {
                                    payload.contextMenuListener?.onDelete(message)
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
                    START_MESSAGE_STYLE -> {
                        userName.text = message.user.name
                        startStyleContainer.visible()
                        bubble.setBackgroundResource(R.drawable.bg_start_incoming_message)
                    }
                    MIDDLE_MESSAGE_STYLE -> {
                        startStyleContainer.gone()
                        bubble.setBackgroundResource(R.drawable.bg_middle_incoming_message)
                    }
                    END_MESSAGE_STYLE -> {
                        startStyleContainer.gone()
                        bubble.setBackgroundResource(R.drawable.bg_end_incoming_message)
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