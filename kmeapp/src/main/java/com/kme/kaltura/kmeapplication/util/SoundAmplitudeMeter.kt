package com.kme.kaltura.kmeapplication.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import java.io.File

class SoundAmplitudeMeter(
    val context: Context
) {

    private var mRecorder: MediaRecorder? = null
    private var rootFolder: File? = null

    fun start() {
        rootFolder = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val filePath = rootFolder?.toURI() ?: return

        val dir = File(filePath)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        if (mRecorder == null) {
            mRecorder = MediaRecorder()
        }

        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mRecorder?.setOutputFile("$dir/$TMP_FILE_NAME")
        mRecorder?.prepare()
        mRecorder?.start()
    }

    fun stop() {
        mRecorder?.let {
            it.stop()
            it.reset()
            it.release()
            File(rootFolder?.toString() + "/$TMP_FILE_NAME").delete()
        }
        mRecorder = null
    }

    fun getAmplitude(): Int {
        return mRecorder?.maxAmplitude ?: 0
    }

    companion object {
        const val MAX_AMPLITUDE = 18000
        private const val TMP_FILE_NAME = "tmp.3gp"
    }

}