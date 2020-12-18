package com.kme.kaltura.kmesdk.rest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.IOException
import java.io.OutputStream

suspend fun downloadFile(
    call: suspend () -> ResponseBody?,
    target: OutputStream,
    success: () -> Unit,
    error: (exception: Exception) -> Unit
) = withContext(Dispatchers.IO) {
    call.invoke()?.byteStream()?.let {
        try {
            target.run {
                write(it.readBytes())
                flush()
                close()
            }
            withContext(Dispatchers.Main) {
                success()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                error(e)
            }
        }
    }
}
