package com.kme.kaltura.kmesdk.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.di.KmeKoinComponent

/**
 * Service wrapper under the room actions
 */
class KmeRoomService : Service(), KmeKoinComponent {

    private val binder: IBinder = RoomServiceBinder()

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        val notification: Notification = createRoomNotification(
            context = this,
            title = getString(R.string.app_name),
            content = getString(R.string.notification_room_join_message)
        )
        startForeground(ROOM_NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    private fun stopService() {
        stopForeground(true)
        stopSelf()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopService()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        releaseScopes()
        stopService()
        super.onDestroy()
    }

    inner class RoomServiceBinder : Binder() {
        val service: KmeRoomService
            get() = this@KmeRoomService
    }

}
