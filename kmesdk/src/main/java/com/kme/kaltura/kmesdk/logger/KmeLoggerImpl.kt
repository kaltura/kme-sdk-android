package com.kme.kaltura.kmesdk.logger

import android.util.Log

class KmeLoggerImpl(val debug: Boolean) : IKmeLogger {

    override fun d(tag: String, message: String) {
        if (debug) {
            Log.d("$LOGGER $tag", message)
        }
    }

    override fun e(tag: String, message: String) {
        if (debug) {
            Log.e("$LOGGER $tag", message)
        }
    }

    override fun e(tag: String, message: String, t: Throwable) {
        if (debug) {
            Log.e("$LOGGER $tag", message, t)
        }
    }

    override fun i(tag: String, message: String, t: Throwable) {
        if (debug) {
            Log.i("$LOGGER $tag", message, t)
        }
    }

    override fun i(tag: String, message: String) {
        if (debug) {
            Log.i("$LOGGER $tag", message)
        }
    }

    companion object {
        private const val LOGGER = "KmeSdk ===>"
    }
}