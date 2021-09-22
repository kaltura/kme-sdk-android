package com.kme.kaltura.kmesdk.service

import android.content.Context
import androidx.work.*
import com.kme.kaltura.kmesdk.controller.IKmeMetadataController
import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.util.concurrent.TimeUnit

class CsrfUpdater(
    private val context: Context
) {
    fun start() {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            CsrfWorker::class.java.simpleName,
            ExistingPeriodicWorkPolicy.REPLACE,
            PeriodicWorkRequestBuilder<CsrfWorker>(INTERVAL_IN_HOURS, TimeUnit.HOURS)
                .setInitialDelay(INTERVAL_IN_HOURS, TimeUnit.HOURS)
                .build()
        )
    }

    companion object {
        private const val INTERVAL_IN_HOURS: Long = 1
    }
}

class CsrfWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params), KmeKoinComponent {

    private val metadataController: IKmeMetadataController by inject()

    override fun doWork(): Result {
        var result = false
        metadataController.keepAlive(
            success = {
                result = true
            },
            error = {
                result = false
            }
        )
        return if (result) Result.success() else Result.failure()
    }

}