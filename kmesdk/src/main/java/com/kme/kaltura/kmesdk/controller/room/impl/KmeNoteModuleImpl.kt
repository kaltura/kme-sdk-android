package com.kme.kaltura.kmesdk.controller.room.impl

import android.content.Context
import android.os.Environment
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeNoteModule
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.di.inject
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.downloadFile
import com.kme.kaltura.kmesdk.rest.response.room.notes.*
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeFileLoaderApiService
import com.kme.kaltura.kmesdk.rest.service.KmeRoomNotesApiService
import com.kme.kaltura.kmesdk.util.messages.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject
import java.io.File
import java.io.FileOutputStream

/**
 * An implementation for actions with notes
 */
class KmeNoteModuleImpl(
    private val context: Context
) : KmeController(), IKmeNoteModule {

    private val roomController: IKmeRoomController by controllersScope().inject()
    private val roomNotesApiService: KmeRoomNotesApiService by inject()
    private val fileLoaderApiService: KmeFileLoaderApiService by inject()

    private val uiScope = CoroutineScope(Dispatchers.Main)

    /**
     * Getting all notes for specific room
     */
    override fun getRoomNotes(
        companyId: Long,
        roomId: Long,
        success: (response: KmeGetRoomNotesResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomNotesApiService.getRoomNotes(companyId, roomId) },
                success,
                error
            )
        }
    }

    /**
     * Getting specific note
     */
    override fun getRoomNote(
        roomId: Long,
        noteId: Long,
        success: (response: KmeGetRoomNoteResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit,
    ) {
        uiScope.launch {
            safeApiCall(
                { roomNotesApiService.getRoomNote(roomId, noteId) },
                success,
                error
            )
        }
    }

    /**
     * Getting an url for download note as pdf file
     */
    override fun getDownloadRoomNoteUrl(
        roomId: Long,
        noteId: Long,
        saveToFiles: Boolean,
        success: (response: KmeRoomNoteDownloadUrlResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomNotesApiService.getDownloadRoomNoteUrl(roomId, noteId, saveToFiles) },
                success,
                error
            )
        }
    }

    /**
     * Creates a new note in the room
     */
    override fun createRoomNote(
        companyId: Long,
        roomId: Long,
        success: (response: KmeRoomNoteCreateResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomNotesApiService.createRoomNote(companyId, roomId) },
                {
                    val name = it.data?.name
                    val date = it.data?.dateCreated
                    val id = it.data?.id
                    if (name != null && date != null && id != null) {
                        roomController.send(buildCreateRoomNoteMessage(
                            roomId,
                            companyId,
                            name,
                            date,
                            id
                        ))
                        success(it)
                    }
                },
                error
            )
        }
    }

    /**
     * Renames specific note
     */
    override fun renameRoomNote(
        roomId: Long,
        companyId: Long,
        noteId: Long,
        name: String,
        success: (response: KmeRoomNoteRenameResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomNotesApiService.renameRoomNote(roomId, noteId, name) },
                {
                    roomController.send(buildRenameRoomNoteMessage(
                        roomId,
                        companyId,
                        noteId,
                        name
                    ))
                    success(it)
                },
                error
            )
        }
    }

    /**
     * Changes content in the note
     */
    override fun updateRoomNoteContent(
        roomId: Long,
        noteId: String,
        content: String,
        updateLogs: Boolean,
        html: String,
        success: (response: KmeRoomNoteUpdateContentResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomNotesApiService.updateRoomNoteContent(roomId, noteId, content, updateLogs, html) },
                success,
                error
            )
        }
    }

    /**
     * Propagate note to all participants
     */
    override fun broadcastNote(roomId: Long, companyId: Long, noteId: Long, name: String) {
        roomController.send(buildBroadcastRoomNoteMessage(
            roomId,
            companyId,
            noteId,
            name
        ))
    }

    /**
     * Subscribes/unsubscribes to the note changes
     */
    override fun subscribeToNoteChanges(
        roomId: Long,
        companyId: Long,
        noteId: Long,
        isSubscribeToNote: Boolean
    ) {
        roomController.send(buildSubscribeRoomNoteMessage(
            roomId,
            companyId,
            noteId,
            isSubscribeToNote
        ))
    }

    /**
     * Download specific note as pdf file
     */
    override fun downloadRoomNote(
        name: String,
        url: String,
        success: () -> Unit,
        error: (exception: Exception) -> Unit
    ) {
        uiScope.launch {
            val outputFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "/$name.pdf"
            )

            downloadFile(
                { fileLoaderApiService.downloadFile(url) },
                FileOutputStream(outputFile),
                success,
                error
            )
        }
    }

    /**
     * Deletes specific note
     */
    override fun deleteRoomNote(
        roomId: Long,
        companyId: Long,
        noteId: Long,
        success: (response: KmeDeleteRoomNoteResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomNotesApiService.deleteRoomNote(roomId, noteId.toLong()) },
                {
                    roomController.send(buildDeleteRoomNoteMessage(
                        roomId,
                        companyId,
                        noteId
                    ))
                    success(it)
                },
                error
            )
        }
    }

}
