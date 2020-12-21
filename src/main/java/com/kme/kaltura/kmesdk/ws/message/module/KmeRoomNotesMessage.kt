package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage

class KmeRoomNotesMessage<T : KmeRoomNotesMessage.NotesPayload> : KmeMessage<T>() {

    data class CreateNotePayload(
        @SerializedName("room_id") val roomId: Long? = null,
        @SerializedName("company_id") val companyId: Long? = null,
        @SerializedName("newNote") val newNoteWrapper: NewNoteWrapper? = null
    ) : NotesPayload()

    data class NewNoteWrapper(
        @SerializedName("newNote") val newNote: NewNote? = null
    )

    data class NewNote(
        @SerializedName("note_name") val noteName: String? = null,
        @SerializedName("date_created") val dateCreated: Long? = null,
        @SerializedName("id") val id: Long? = null
    )

    data class RenameNotePayload(
        @SerializedName("roomId") val roomId: Long? = null,
        @SerializedName("companyId") val companyId: Long? = null,
        @SerializedName("eventData") val eventData: NoteEventData? = null
    ) : NotesPayload()

    data class DeleteNotePayload(
        @SerializedName("roomId") val roomId: Long? = null,
        @SerializedName("companyId") val companyId: Long? = null,
        @SerializedName("eventData") val eventData: NoteEventData? = null
    ) : NotesPayload()

    data class NoteEventData(
        @SerializedName("noteId") val noteId: String? = null,
        @SerializedName("noteNewName") val noteNewName: String? = null
    )

    open class NotesPayload : Payload()

}
