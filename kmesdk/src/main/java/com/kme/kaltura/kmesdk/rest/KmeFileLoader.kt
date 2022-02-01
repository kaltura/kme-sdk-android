package com.kme.kaltura.kmesdk.rest

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.*


/**
 * Main REST function. Download files from the server
 */
suspend fun downloadFile(
    call: suspend () -> ResponseBody?,
    fileName: String,
    context: Context,
    success: (String?) -> Unit,
    error: (exception: Exception) -> Unit
) = withContext(Dispatchers.IO) {
    call.invoke()?.byteStream()?.let {
        try {

            val filePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                storeFileApi29(context, it, fileName)
            } else {
                storeFile(it, fileName)
            }
            withContext(Dispatchers.Main) {
                success(filePath)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                error(e)
            }
        }
    }
}

/**
 * Save the file api level 29 and down
 * @param inputStream file from the server
 * @param fileName  name of the file
 * @return full path for the file
 */
private fun storeFile(inputStream: InputStream, fileName: String): String {
    val outputFile = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "/$fileName"
    )
    val fos = FileOutputStream(outputFile)
    fos.run {
        write(inputStream.readBytes())
        flush()
        close()
    }
    return outputFile.absolutePath
}

/**
 * Save the file api level 29 and above
 * @param context
 * @param inputStream file from the server
 * @param fileName  name of the file
 * @return full path for the file
 */
@RequiresApi(Build.VERSION_CODES.Q)
private fun storeFileApi29(context: Context, inputStream: InputStream, fileName: String): String? {
    val bis = BufferedInputStream(inputStream)
    val values = ContentValues()
    var filePath: String? = null
    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
    uri?.let {
        val outputStream = context.contentResolver.openOutputStream(it)
        if (outputStream != null) {
            val bos = BufferedOutputStream(outputStream)
            val buffer = ByteArray(1024)
            var bytes = bis.read(buffer)
            while (bytes >= 0) {
                bos.write(buffer, 0, bytes)
                bos.flush()
                bytes = bis.read(buffer)
            }
            bos.close()
        }
    }
    bis.close()
    uri?.let {
        getFilePathByUri(context, it)?.let { path ->
            filePath = path
        }
    }

    return filePath
}

/**
 * Gets the corresponding path to a file from the given content:// URI
 * @param uri The content:// URI to find the file path from
 * @return the file path as a string
 */
@RequiresApi(Build.VERSION_CODES.Q)
private fun getFilePathByUri(context: Context, uri: Uri): String? {
    var filePath: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    if (cursor != null && cursor.count > 0) {
        cursor.moveToFirst()
        filePath = cursor.getString(cursor.getColumnIndexOrThrow("_data"))
        cursor.close()
    }
    return filePath
}
