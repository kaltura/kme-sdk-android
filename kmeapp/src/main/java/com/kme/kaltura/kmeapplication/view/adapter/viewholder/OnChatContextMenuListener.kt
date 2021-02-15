package com.kme.kaltura.kmeapplication.view.adapter.viewholder

import com.kme.kaltura.kmeapplication.data.MappedChatMessage
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant

interface OnChatContextMenuListener {

    fun getCurrentParticipant(): KmeParticipant?

    fun isParticipantJoined(userId: Long): Boolean

    fun onReply(message: MappedChatMessage, replyAll: Boolean = false)

    fun onStartPrivateChat(userId: Long)

    fun onDelete(message: MappedChatMessage)

}