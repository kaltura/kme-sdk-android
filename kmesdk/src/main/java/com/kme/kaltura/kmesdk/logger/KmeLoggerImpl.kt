package com.kme.kaltura.kmesdk.logger

import timber.log.Timber


class KmeLoggerImpl : IKmeLogger {

    init {
        if (Timber.treeCount == 0)
            Timber.plant(Timber.DebugTree())
    }

    override fun d(tag: String, t: Throwable?) {
        Timber.tag("$LOGGER $tag").d(t)
    }

    override fun d(tag: String, message: String?, vararg args: Any?) {
        Timber.tag("$LOGGER $tag").d(message, args)
    }

    override fun e(tag: String, t: Throwable?) {
        Timber.tag("$LOGGER $tag").e(t)
    }

    override fun e(tag: String, message: String?, vararg args: Any?) {
        Timber.tag("$LOGGER $tag").e(message, args)
    }

    override fun i(tag: String, t: Throwable?) {
        Timber.tag("$LOGGER $tag").i(t)
    }

    override fun i(tag: String, message: String?, vararg args: Any?) {
        Timber.tag("$LOGGER $tag").i(message, args)
    }

    companion object {
        private const val LOGGER = "KmeSdk ===>"
    }
}