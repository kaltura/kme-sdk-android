package com.kme.kaltura.kmeapplication.viewmodel

import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_COMPOSING
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.kme.kaltura.kmeapplication.util.extensions.ifNonNull
import com.kme.kaltura.kmeapplication.util.toSingleEvent
import com.kme.kaltura.kmeapplication.view.view.note.OrderedListSpan
import com.kme.kaltura.kmeapplication.view.view.note.UnorderedListSpan
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeRoomNote
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomNotesMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomNotesMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeNoteBlockType
import com.kme.kaltura.kmesdk.ws.message.type.KmeNoteStyle

class RoomNoteViewModel(
    private val kmeSdk: KME,
    private val gson: Gson
) : ViewModel() {

    private val isLoading = MutableLiveData<Boolean>()
    val isLoadingLiveData get() = isLoading as LiveData<Boolean>

    private val roomNotes = MutableLiveData<List<KmeRoomNote>>()
    val roomNotesLiveData get() = roomNotes as LiveData<List<KmeRoomNote>>

    private val addToFilesState = MutableLiveData<Boolean>()
    val addToFilesStateLiveData = addToFilesState.toSingleEvent()

    private val downloadState = MutableLiveData<Boolean>()
    val downloadStateLiveData = downloadState.toSingleEvent()

    private val deleteNoteState = MutableLiveData<Boolean>()
    val deleteNoteStateLiveData = deleteNoteState.toSingleEvent()

    private val enableAdminControls = MutableLiveData<Boolean>()
    val adminControlsLiveData get() = enableAdminControls as LiveData<Boolean>

    private val openNote = MutableLiveData<KmeRoomNote>()
    val openNoteLiveData get() = openNote as LiveData<KmeRoomNote>

    private val broadcastNote = MutableLiveData<KmeRoomNote>()
    val broadcastNoteLiveData = broadcastNote.toSingleEvent()

    private val noteTextChanged = MutableLiveData<SpannableString>()
    val noteTextChangedLiveData = noteTextChanged as LiveData<SpannableString>

    private val deletedNote = MutableLiveData<String>()
    val deletedNoteLiveData get() = deletedNote as LiveData<String>

    private var noteBlocks: MutableList<NoteEditBlock> = mutableListOf()

    private var companyId: Long = 0
    private var roomId: Long = 0

    fun setRoomData(companyId: Long, roomId: Long) {
        this.companyId = companyId
        this.roomId = roomId

        subscribeForEvents()
        getRoomNotes()
    }

    fun getRoomNotes() {
        isLoading.value = true
        kmeSdk.roomController.noteModule.getRoomNotes(
            companyId, roomId,
            success = {
                isLoading.value = false
                roomNotes.value = it.data?.notes
                enableAdminControls.value =
                    kmeSdk.userController.isModerator() ||
                            kmeSdk.userController.isAdminFor(companyId)
            }, error = {
                isLoading.value = false
            }
        )
    }

    private fun subscribeForEvents() {
        kmeSdk.roomController.listen(
            noteHandler,
            KmeMessageEvent.ROOM_NOTE_CREATED,
            KmeMessageEvent.ROOM_NOTE_RENAMED,
            KmeMessageEvent.BROADCAST_ROOM_NOTE_TO_ALL,
            KmeMessageEvent.ROOM_NOTE_SEND_TO_LISTENERS,
            KmeMessageEvent.ROOM_NOTE_DELETED
        )
    }

    fun createNote() {
        isLoading.value = true
        kmeSdk.roomController.noteModule.createRoomNote(
            companyId, roomId,
            success = {
                isLoading.value = false
                val name = it.data?.name
                val date = it.data?.dateCreated
                val id = it.data?.id
                openNote.value = KmeRoomNote(
                    name = name,
                    dateCreated = date,
                    id = id.toString()
                )
            }, error = {
                isLoading.value = false
            }
        )
    }

    fun renameNote(noteId: String, newName: String) {
        isLoading.value = true
        kmeSdk.roomController.noteModule.renameRoomNote(
            roomId, companyId, noteId, newName,
            success = {
                isLoading.value = false
            }, error = {
                isLoading.value = false
            }
        )
    }

    fun downloadNote(roomNote: KmeRoomNote) {
        isLoading.value = true
        roomNote.id?.let {
            kmeSdk.roomController.noteModule.getDownloadRoomNoteUrl(
                roomId, it.toLong(), false,
                success = { response ->
                    ifNonNull(roomNote.name, response.data?.message) { name, url ->
                        downloadNote(name, url)
                    }
                }, error = {
                    isLoading.value = false
                    downloadState.value = false
                }
            )
        }
    }

    private fun downloadNote(name: String, url: String) {
        kmeSdk.roomController.noteModule.downloadRoomNote(
            name, url,
            success = {
                isLoading.value = false
                downloadState.value = true
            }, error = {
                isLoading.value = false
                downloadState.value = false
            }
        )
    }

    fun addNoteToFiles(roomNote: KmeRoomNote) {
        isLoading.value = true
        roomNote.id?.let {
            kmeSdk.roomController.noteModule.getDownloadRoomNoteUrl(
                roomId, it.toLong(), true,
                success = {
                    addToFilesState.value = true
                    isLoading.value = false
                }, error = {
                    addToFilesState.value = false
                    isLoading.value = false
                }
            )
        }
    }

    fun deleteNote(roomNote: KmeRoomNote) {
        isLoading.value = true
        roomNote.id?.let { noteId ->
            kmeSdk.roomController.noteModule.deleteRoomNote(
                roomId, companyId, noteId,
                success = {
                    deleteNoteState.value = true
                    isLoading.value = false
                }, error = {
                    deleteNoteState.value = false
                    isLoading.value = false
                }
            )
        }
    }

    fun openNote(roomNote: KmeRoomNote) {
        openNote.value = roomNote
    }

    fun broadcastNote(roomNote: KmeRoomNote) {
        ifNonNull(roomNote.id, roomNote.name) { id, name ->
            kmeSdk.roomController.noteModule.broadcastNote(roomId, companyId, id, name)
        }
    }

    fun subscribeToNoteChanges(noteId: String, isSubscribe: Boolean) {
        kmeSdk.roomController.noteModule.subscribeToNoteChanges(
            roomId,
            companyId,
            noteId,
            isSubscribe
        )
    }

    fun setNoteContent(content: String) {
        gson.fromJson(content, NoteBlocks::class.java)?.blocks?.let {
            buildTextFromBlocks(it)
        } ?: run {
            buildTextFromBlocks(mutableListOf())
        }
    }

    private fun buildTextFromBlocks(blocks: List<NoteEditBlock>) {
        noteBlocks = blocks.toMutableList()
        noteTextChanged.value = getStyledText()
    }

    private fun getStyledText(): SpannableString {
        val builder = SpannableStringBuilder(String())
        var orderIndex = 1

        noteBlocks.forEach { editBlock ->
            val blockText = SpannableString(editBlock.text)

            if (!editBlock.inlineStyleRanges.isNullOrEmpty()) {
                editBlock.inlineStyleRanges.forEach { inlineStyle ->
                    val style = when (inlineStyle.style) {
                        KmeNoteStyle.ITALIC -> StyleSpan(Typeface.ITALIC)
                        KmeNoteStyle.BOLD -> StyleSpan(Typeface.BOLD)
                        KmeNoteStyle.UNDERLINE -> UnderlineSpan()
                        else -> StyleSpan(Typeface.NORMAL)
                    }
                    val start = inlineStyle.offset
                    val end = start + inlineStyle.length
                    blockText.setSpan(style, start, end, 0)
                }
            }

            val type: Any = when (editBlock.type) {
                KmeNoteBlockType.ORDERED_LIST ->
                    OrderedListSpan("${orderIndex}. ").also { orderIndex += 1 }
                KmeNoteBlockType.UNORDERED_LIST -> UnorderedListSpan().also { orderIndex = 1 }
                KmeNoteBlockType.UNSTYLED -> StyleSpan(Typeface.NORMAL).also { orderIndex = 1 }
            }
            blockText.setSpan(type, 0, editBlock.text.length, SPAN_COMPOSING)

            builder.append(blockText).append("\n")
        }
        return SpannableString.valueOf(builder)
    }

    private val noteHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.ROOM_NOTE_CREATED -> {
                    getRoomNotes()
                }
                KmeMessageEvent.ROOM_NOTE_RENAMED -> {
                    val msg: KmeRoomNotesMessage<NotePayload>? = message.toType()
                    ifNonNull(
                        msg?.payload?.eventData?.noteId,
                        msg?.payload?.eventData?.noteNewName
                    ) { noteId, newName ->
                        handleRenameNote(noteId, newName)
                    }
                }
                KmeMessageEvent.BROADCAST_ROOM_NOTE_TO_ALL -> {
                    val msg: KmeRoomNotesMessage<NotePayload>? = message.toType()
                    msg?.payload?.eventData?.noteId?.let { handleBroadcastNote(it) }
                }
                KmeMessageEvent.ROOM_NOTE_SEND_TO_LISTENERS -> {
                    val msg: KmeRoomNotesMessage<NotePayload>? = message.toType()
                    val manifest: List<String> = msg?.payload?.eventData?.manifest ?: listOf()
                    val changes: MutableList<NoteEditDelta> = mutableListOf()
                    msg?.payload?.eventData?.deltas?.keyValueContent?.let {
                        it.entries.forEach { editDelta -> changes.add(editDelta.value) }
                    }
                    handleNoteChange(manifest, changes)
                }
                KmeMessageEvent.ROOM_NOTE_DELETED -> {
                    val msg: KmeRoomNotesMessage<NotePayload>? = message.toType()
                    msg?.payload?.eventData?.noteId?.let { handleDeleteNote(it) }
                }
                else -> {
                }
            }
        }
    }

    private fun handleRenameNote(noteId: String, newNoteName: String) {
        val notes: MutableList<KmeRoomNote> = roomNotes.value?.toMutableList() ?: mutableListOf()
        notes.find { note -> note.id.equals(noteId) }?.let { target ->
            target.id = noteId
            target.name = newNoteName
            roomNotes.value = notes
        }
    }

    private fun handleBroadcastNote(noteId: String) {
        if (kmeSdk.userController.isModerator() || kmeSdk.userController.isAdminFor(companyId)) {
            return
        }

        val notes: MutableList<KmeRoomNote> = roomNotes.value?.toMutableList() ?: mutableListOf()
        notes.find { note -> note.id.equals(noteId) }?.let { target ->
            openNote.value = target
            broadcastNote.value = target
        }
    }

    private fun handleNoteChange(manifest: List<String>, changes: List<NoteEditDelta>) {
        updateBlocks(changes)
        applyManifest(manifest)
        buildTextFromBlocks(noteBlocks)
    }

    private fun updateBlocks(updates: List<NoteEditDelta>) {
        updates.forEach { update ->
            val index = noteBlocks.indexOfFirst { current -> update.block.key == current.key }
            if (index >= 0) {
                noteBlocks[index] = update.block
            } else {
                noteBlocks.add(update.block)
            }
        }
    }

    private fun applyManifest(manifest: List<String>) {
        val orderedBlocks: MutableList<NoteEditBlock> = mutableListOf()
        manifest.forEach { key ->
            noteBlocks.find { block -> block.key == key }?.let { orderedBlocks.add(it) }
        }
        noteBlocks = orderedBlocks
    }

    private fun handleDeleteNote(noteId: String) {
        val notes: MutableList<KmeRoomNote> = roomNotes.value?.toMutableList() ?: mutableListOf()
        val isRemoved = notes.removeAll { note -> note.id.equals(noteId) }
        if (isRemoved) {
            deletedNote.value = noteId
            roomNotes.value = notes
        }
    }

}
