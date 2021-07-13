package com.kme.kaltura.kmesdk.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.controller.room.IKmePeerConnectionModule
import com.kme.kaltura.kmesdk.controller.room.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.ws.IKmeWSConnectionListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import org.koin.android.ext.android.inject

/**
 * Service wrapper under the room actions
 */
class KmeRoomService : Service(), KmeKoinComponent, IKmeWebSocketModule {

    private val webSocketModule: IKmeWebSocketModule by inject()
    private val peerConnectionModule: IKmePeerConnectionModule by inject()

    private val binder: IBinder = RoomServiceBinder()

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        val notification: Notification = createRoomNotification(context = this, title = getString(R.string.app_name), content = getString(R.string.notification_room_join_message))
        startForeground(ROOM_NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    /**
     * Establish socket connection
     */
    override fun connect(
        url: String,
        companyId: Long,
        roomId: Long,
        isReconnect: Boolean,
        token: String,
        listener: IKmeWSConnectionListener
    ) {
        webSocketModule.connect(url, companyId, roomId, isReconnect, token, listener)
    }

    /**
     * Check is socket connected
     */
    override fun isConnected(): Boolean = webSocketModule.isConnected()

    /**
     * Send message via socket
     */
    override fun send(message: KmeMessage<out KmeMessage.Payload>) {
        webSocketModule.send(message)
    }

    /**
     * Disconnect from the room. Destroy all related connections
     */
    override fun disconnect() {
        webSocketModule.disconnect()
        peerConnectionModule.disconnectAll()
        stopForeground(true)
        stopSelf()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        disconnect()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        disconnect()
        super.onDestroy()
    }

    inner class RoomServiceBinder : Binder() {
        val service: KmeRoomService
            get() = this@KmeRoomService
    }

}