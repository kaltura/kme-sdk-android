package com.kme.kaltura.kmesdk.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.kme.kaltura.kmesdk.R

const val ROOM_NOTIFICATION_ID = 1000
private const val CHANNEL_ID = "room_channel_id"

/**
 * Creates foreground service notification
 */
fun createRoomNotification(
    context: Context
): Notification {

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setSound(null)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setOngoing(true)
        .setAutoCancel(true)

    createNotificationChannel(context, CHANNEL_ID)
    return builder.build()
}

/**
 * Creates service notification channel
 *
 * @param context application context
 * @param channelId id of a notification channel
 */
private fun createNotificationChannel(context: Context, channelId: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_LOW
        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )
        var notificationChannel = notificationManager.getNotificationChannel(channelId)
        if (notificationChannel == null) {
            val channelName = context.getString(R.string.app_name)
            notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(false)
            notificationChannel.enableVibration(false)
            notificationChannel.setSound(null, null)
        }
        notificationManager.createNotificationChannel(notificationChannel)
    }
}
