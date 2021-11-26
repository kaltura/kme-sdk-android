package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeNoteBlockType
import com.kme.kaltura.kmesdk.ws.message.type.KmeNoteStyle
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserRole

class KmeRoomNotesMessage<T : KmeRoomNotesMessage.NotesPayload> : KmeMessage<T>() {

    data class CreateNotePayload(
        @SerializedName("room_id", alternate = ["roomId"]) val roomId: Long? = null,
        @SerializedName("company_id", alternate = ["companyId"]) val companyId: Long? = null,
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

    data class NotePayload(
        @SerializedName("roomId") val roomId: Long? = null,
        @SerializedName("companyId") val companyId: Long? = null,
        @SerializedName("eventData") val eventData: NoteEventData? = null
    ) : NotesPayload()

    data class NoteEventData(
        @SerializedName("noteId") val noteId: Long? = null,
        @SerializedName("noteName") val noteName: String? = null,
        @SerializedName("noteNewName") val noteNewName: String? = null,
        @SerializedName("isSubscribeToNote") val isSubscribeToNote: Boolean? = null,
        @SerializedName("manifest") val manifest: List<String>? = null,
        @SerializedName("deltas") val deltas: NoteEditKeyValueContent? = null,
        @SerializedName("editor") val editor: NoteEditor? = null
    )

    data class NoteEditKeyValueContent(
        @SerializedName("keyValueContent") val keyValueContent: LinkedHashMap<String, NoteEditDelta>
    )

    data class NoteEditDelta(
        @SerializedName("position") val position: Int,
        @SerializedName("block") val block: NoteEditBlock
    )

    data class NoteEditBlock(
        @SerializedName("key") var key: String,
        @SerializedName("text") var text: String,
        @SerializedName("type") var type: KmeNoteBlockType = KmeNoteBlockType.UNSTYLED,
        @SerializedName("depth") val depth: Long,
        @SerializedName("inlineStyleRanges") var inlineStyleRanges: List<NoteInlineStyle>,
        @SerializedName("entityRanges") var entityRanges: List<NoteInlineStyle> = arrayListOf(),
        @SerializedName("data") var data: Any? = null
    )

    data class NoteInlineStyle(
        @SerializedName("offset") val offset: Int,
        @SerializedName("length") val length: Int,
        @SerializedName("style") val style: KmeNoteStyle
    )

    data class NoteEditor(
        @SerializedName("userId") val userId: Long? = null,
        @SerializedName("userType") val userType: KmeUserRole? = null,
        @SerializedName("userName") val userName: String? = null
    )

    data class NoteBlocks(
        @SerializedName("blocks") val blocks: List<NoteEditBlock>? = null,
        @SerializedName("entityMap") val entityMap: Any? = null,
    )

    open class NotesPayload : Payload()

}
