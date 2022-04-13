package com.kme.kaltura.kmesdk.logger

import android.util.Log

/**
 * An interface for logcat
 */
interface IKmeLogger {

    /** Log a debug message */
    fun d(tag: String, message: String)

    /** Log an error message */
    fun e(tag: String, message: String, t: Throwable)

    /** Log an info exception. */
    fun i(tag: String, message: String, t: Throwable)

    /** Log an info message with optional format args. */
    fun i(tag: String, message: String)
}