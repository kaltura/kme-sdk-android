package com.kme.kaltura.kmesdk.controller.impl

import android.content.Context
import android.os.Environment
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.downloadFile
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeGetRoomNotesResponse
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeRoomNoteDownloadUrlResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeFileLoaderApiService
import com.kme.kaltura.kmesdk.rest.service.KmeRoomNotesApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject
import java.io.File
import java.io.FileOutputStream

class KmeRoomNotesControllerImpl(
    private val context: Context
) : KmeController(), IKmeRoomNotesController {

    private val roomNotesApiService: KmeRoomNotesApiService by inject()
    private val loaderApiService: KmeFileLoaderApiService by inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

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
                { loaderApiService.downloadFile(url) },
                FileOutputStream(outputFile),
                success,
                error
            )
        }
    }

}
