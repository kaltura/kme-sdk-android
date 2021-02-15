package com.kme.kaltura.kmesdk.util.messages

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEventType
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomNotesMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomNotesMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeConstraint

internal fun buildCreateRoomNoteMessage(
    roomId: Long,
    companyId: Long,
    noteName: String,
    dateCreated: Long,
    noteId: Long
): KmeRoomNotesMessage<CreateNotePayload> {
    return KmeRoomNotesMessage<CreateNotePayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        type = KmeMessageEventType.VOID
        module = KmeMessageModule.NOTES
        name = KmeMessageEvent.ROOM_NOTE_CREATED
        payload = CreateNotePayload(
            roomId,
            companyId,
            NewNoteWrapper(
                NewNote(noteName, dateCreated, noteId)
            )
        )
    }
}

internal fun buildRenameRoomNoteMessage(
    roomId: Long,
    companyId: Long,
    noteId: String,
    noteNewName: String
): KmeRoomNotesMessage<NotePayload> {
    return KmeRoomNotesMessage<NotePayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        type = KmeMessageEventType.VOID
        module = KmeMessageModule.NOTES
        name = KmeMessageEvent.ROOM_NOTE_RENAMED
        payload = NotePayload(
            roomId,
            companyId,
            NoteEventData(noteId, noteNewName = noteNewName)
        )
    }
}

internal fun buildBroadcastRoomNoteMessage(
    roomId: Long,
    companyId: Long,
    noteId: String,
    noteName: String
): KmeRoomNotesMessage<NotePayload> {
    return KmeRoomNotesMessage<NotePayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        type = KmeMessageEventType.VOID
        module = KmeMessageModule.NOTES
        name = KmeMessageEvent.BROADCAST_ROOM_NOTE_TO_ALL
        payload = NotePayload(
            roomId,
            companyId,
            NoteEventData(noteId, noteName = noteName)
        )
    }
}

internal fun buildSubscribeRoomNoteMessage(
    roomId: Long,
    companyId: Long,
    noteId: String,
    isSubscribeToNote: Boolean
): KmeRoomNotesMessage<NotePayload> {
    return KmeRoomNotesMessage<NotePayload>().apply {
        type = KmeMessageEventType.VOID
        module = KmeMessageModule.NOTES
        name = KmeMessageEvent.ROOM_NOTE_SUBSCRIBE
        payload = NotePayload(
            roomId,
            companyId,
            NoteEventData(noteId, isSubscribeToNote = isSubscribeToNote)
        )
    }
}

internal fun buildSendNoteChangesMessage(
    roomId: Long,
    companyId: Long,
    noteId: String,
    manifest: List<String>,
    editor: NoteEditor,
    deltas: NoteEditKeyValueContent
): KmeRoomNotesMessage<NotePayload> {
    return KmeRoomNotesMessage<NotePayload>().apply {
        type = KmeMessageEventType.VOID
        module = KmeMessageModule.NOTES
        name = KmeMessageEvent.ROOM_NOTE_SEND_TO_LISTENERS

        payload = NotePayload(
            roomId,
            companyId,
            NoteEventData(
                noteId,
                editor = editor,
                manifest = manifest,
                deltas = deltas
            )
        )
    }
}

internal fun buildDeleteRoomNoteMessage(
    roomId: Long,
    companyId: Long,
    noteId: String
): KmeRoomNotesMessage<NotePayload> {
    return KmeRoomNotesMessage<NotePayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        type = KmeMessageEventType.VOID
        module = KmeMessageModule.NOTES
        name = KmeMessageEvent.ROOM_NOTE_DELETED
        payload = NotePayload(
            roomId,
            companyId,
            NoteEventData(noteId)
        )
    }
}
