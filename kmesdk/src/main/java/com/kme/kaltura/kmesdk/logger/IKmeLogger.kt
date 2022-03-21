package com.kme.kaltura.kmesdk.logger

import android.util.Log

/**
 * An interface for logcat
 */
interface IKmeLogger {

    /** Log a debug exception. */
    fun d(tag: String, t: Throwable?)

    /** Log a debug message with optional format args. */
    fun d(tag: String, message: String?, vararg args: Any?)

    /** Log an error exception. */
    fun e(tag: String, t: Throwable?)

    /** Log an error message with optional format args. */
    fun e(tag: String, message: String?, vararg args: Any?)

    /** Log an info exception. */
    fun i(tag: String, t: Throwable?)

    /** Log an info message with optional format args. */
    fun i(tag: String, message: String?, vararg args: Any?)
}