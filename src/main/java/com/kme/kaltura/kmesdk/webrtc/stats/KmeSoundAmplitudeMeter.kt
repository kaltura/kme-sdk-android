package com.kme.kaltura.kmesdk.webrtc.stats

import android.media.MediaRecorder

class KmeSoundAmplitudeMeter {

    private var mRecorder: MediaRecorder? = null

    fun start() {
        if (mRecorder == null) {
            mRecorder = MediaRecorder()
        }
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mRecorder?.setOutputFile("/dev/null")
        mRecorder?.prepare()
        mRecorder?.start()
    }

    fun stop() {
        mRecorder?.let {
            it.stop()
            it.release()
        }
        mRecorder = null
    }

    fun getAmplitude(): Int {
        return mRecorder?.maxAmplitude ?: 0
    }

}
